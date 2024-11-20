package com.github.ldjmthgh.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.github.ldjmthgh.common.CommonConstant;
import com.github.ldjmthgh.common.CommonResponse;
import com.github.ldjmthgh.model.SharedDataInfo;
import com.github.ldjmthgh.model.UserAuthenticationInfo;
import com.github.ldjmthgh.model.dto.UserDTO;
import com.github.ldjmthgh.model.po.UserPO;
import com.github.ldjmthgh.model.vo.UserVO;
import com.github.ldjmthgh.repossitory.UserRepository;
import com.github.ldjmthgh.service.UserService;
import com.github.ldjmthgh.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Value("${project.path.avatar}")
    String avatarDirPath;


    @Value("${spring.mail.host}")
    String myEmail;


    private JavaMailSenderImpl mailSender;
    private UserRepository userRepository;
    private SharedDataInfo sharedDataInfo;


    @Autowired
    public void getUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void getJavaMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    public void getSharedDataInfo(SharedDataInfo sharedDataInfo) {
        this.sharedDataInfo = sharedDataInfo;
    }

    /**
     * @param dto 用户数据
     * @return 注册
     */
    @Override
    public CommonResponse<Boolean> register(UserDTO dto) {
        if (!CommonConstant.checkUserName(dto.getUserName())) {
            return CommonResponse.buildUnSuccess("用户名不符合规范， 仅允许包含数字、英文字母及下划线，3-16个字符，需以字母开头");
        }
        String checkMsg = dto.check();
        if (null != checkMsg) {
            return CommonResponse.buildUnSuccess(checkMsg);
        }
        UserPO old = userRepository.findByUserName(dto.getUserName());
        if (null != old) {
            return CommonResponse.buildUnSuccess("用户名重复");
        }
        old = userRepository.findByEmail(dto.getEmail());
        if (null != old) {
            return CommonResponse.buildUnSuccess("邮箱地址重复");
        }
        UserPO po = new UserPO(dto);
        po.setUserPassword(getSha1Re(dto.getUserPassword()));
        try {
            File die = new File(avatarDirPath);
            if (!die.exists()) {
                if (!die.mkdirs()) {
                    return CommonResponse.buildUnSuccess("创建系统资源失败");
                }
            }
            File avatarFile = new File(die, "avatar.jpg");
            File defaultAvatarFile = new ClassPathResource("/avatar/avatar.jpg").getFile();
            if (defaultAvatarFile.exists()) {
                if (!avatarFile.exists()) {
                    FileUtil.copy(defaultAvatarFile, avatarFile, true);
                }
                po.setAvatarStorageUrl(avatarFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("默认头像文件不存在， {}", e.getMessage(), e.getCause());
        }
        userRepository.save(po);
        return CommonResponse.buildSuccess();
    }

    /**
     * @param response 返回
     * @param dto      用户数据
     * @return 三段式返回
     */
    @Override
    public CommonResponse<String> login(HttpServletResponse response, UserDTO dto) {
        String db_pwd = getSha1Re(dto.getUserPassword());
        UserPO po = userRepository.findByUserNameAndUserPassword(dto.getUserName(), db_pwd);
        if (null == po) {
            return CommonResponse.buildUnSuccess("用户不存在或账号密码错误");
        }
        String authentication = getSha1Re(dto.getUserName() + db_pwd);
        // 唯一性字段 三小时
        // 唯一性字段 三小时
        UserAuthenticationInfo info = new UserAuthenticationInfo();
        info.setId(po.getId())
                .setUserName(dto.getUserName())
                .setNickName(dto.getNickName())
                .setAuthentication(authentication)
                .setExpireTime(System.currentTimeMillis() + CommonConstant.THREE_HOUR_SECOND);
        sharedDataInfo.userAuthenticationMap.put(authentication, info);
        return CommonResponse.buildSuccess(authentication);
    }

    /**
     * @param authentication 授权
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> logout(String authentication) {
        // 存在注销用户 并且 授权匹配
        sharedDataInfo.userAuthenticationMap.remove(authentication);
        return CommonResponse.buildSuccess();
    }

    /**
     * 获取用户信息
     *
     * @param authentication 授权头
     * @return 三段式返回
     */
    @Override
    public CommonResponse<UserVO> getUserInfo(String authentication) {
        return CommonResponse.buildSuccess(new UserVO(getUserName(authentication)));
    }

    /**
     * @param authentication 授权
     * @param file           头像文件
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> changeAvatar(String authentication, MultipartFile file) {
        File die = new File(avatarDirPath);
        if (!die.exists()) {
            if (!die.mkdirs()) {
                return CommonResponse.buildUnSuccess("创建系统资源失败");
            }
        }
        // FIXME: 文件可能存在冲突
        File avatarFile = new File(die, System.currentTimeMillis() + "-" + authentication);
        if (avatarFile.exists()) {
            avatarFile = new File(die, System.currentTimeMillis() + "-" + authentication + "-" + UUID.randomUUID().toString());
        }
        try {
            if (!avatarFile.createNewFile()) {
                return CommonResponse.buildUnSuccess("创建系统资源失败");
            }
            FileUtil.writeFromStream(file.getInputStream(), avatarFile);
            UserPO po = getUserName(authentication);
            po.setAvatarStorageUrl(avatarFile.getAbsolutePath());
            userRepository.save(po);
            return CommonResponse.buildSuccess();
        } catch (Exception e) {
            log.error("创建系统资源失败： {}", e.getMessage(), e.getCause());
            return CommonResponse.buildUnSuccess("创建系统资源失败");
        }
    }

    /**
     * @param authentication 授权
     * @param userName       新用户名
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> changeUserName(String authentication, String userName) {
        if (!CommonConstant.checkUserName(userName)) {
            return CommonResponse.buildUnSuccess("用户名不符合规范， 仅允许包含数字、英文字母及下划线，且长度在2-15之间");
        }
        UserPO old = getUserName(authentication);
        if (Objects.equals(userName, old.getUserName())) {
            return CommonResponse.buildUnSuccess("无效的更新，提交的用户名与旧用户名一致");
        }
        UserPO po = userRepository.findByUserName(userName);
        if (null != po) {
            return CommonResponse.buildUnSuccess("用户名重复");
        }
        old.setUserName(userName);
        userRepository.save(old);
        return CommonResponse.buildSuccess();
    }

    /**
     * @param authentication 授权
     * @param nickName       昵称
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> changeNickName(String authentication, String nickName) {
        if (nickName.length() > 28 || nickName.isEmpty()) {
            return CommonResponse.buildUnSuccess("昵称限长1 - 28");
        }
        UserPO po = getUserName(authentication);
        if (Objects.equals(nickName, po.getNickName())) {
            return CommonResponse.buildUnSuccess("无效的更新，提交的昵称与旧昵称一致");
        }
        po.setNickName(nickName);
        userRepository.save(po);
        return CommonResponse.buildSuccess();
    }

    /**
     * @param authentication 授权
     * @param oldPwd         原密码
     * @param newPwd         新密码
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> resetPassword(String authentication, String oldPwd, String newPwd) {
        UserPO po = getUserName(authentication);
        if (Objects.equals(oldPwd, newPwd)) {
            return CommonResponse.buildUnSuccess("请提交不一致的新旧密码");
        }
        if (!Objects.equals(getSha1Re(oldPwd), po.getUserPassword())) {
            return CommonResponse.buildUnSuccess("原密码不正确");
        }
        if (!CommonConstant.checkPwd(newPwd)) {
            return CommonResponse.buildUnSuccess("密码格式异常，仅允许包含大小写字母、数字、特殊符号(!@#$%^&*?)，长度位8-16，且至少包含1个大写字母，1个小写字母，1个数字和1个特殊字符；");
        }
        po.setUserPassword(getSha1Re(newPwd));
        userRepository.save(po);
        // 清除认证
        sharedDataInfo.userAuthenticationMap.remove(authentication);
        return CommonResponse.buildSuccess();
    }

    /**
     * 发送重置密码（随机密码）  需要图片验证码
     *
     * @param authentication 验证
     * @param email          邮件
     * @param request        请求
     * @return 返回
     */
    @Override
    public CommonResponse<Boolean> sendEmailRestPwd(String email, String authentication, HttpServletRequest request) {
        UserPO po = userRepository.findByEmail(email);
        if (null == po) {
            return CommonResponse.buildUnSuccess("无效的邮箱地址");
        }
        String ip = IpUtil.getRequestIP(request);
        // 封禁ip
        if (null == ip || ip.isEmpty()) {
            throw new RuntimeException("未知的访问来源，禁止请求；请检查代理及其他！");
        }
        String codeStr = RandomUtil.randomString(9);
        sendEmail(po.getEmail(), "修改密码", "您的新密码为：</br><h2>" + codeStr + "</h2></br> 请即时更新密码！", true);
        po.setUserPassword(getSha1Re(codeStr));
        userRepository.save(po);
        sharedDataInfo.userAuthenticationMap.remove(authentication);
        return CommonResponse.buildUnSuccess("无效的验证码");
    }

    /**
     * 判断是否存在用户 存在及默认为已初始化
     *
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> init() {
        List<UserPO> list = userRepository.findAll();
        return CommonResponse.buildSuccess(!list.isEmpty());
    }

    @Override
    public CommonResponse<Boolean> checkAuthentication(String authentication) {
        if (null == authentication || authentication.isEmpty()) {
            return CommonResponse.buildUnSuccess("无效的授权码");
        }
        if (sharedDataInfo.userAuthenticationMap.containsKey(authentication)) {
            return CommonResponse.buildSuccess();
        }
        return CommonResponse.buildUnSuccess("无效的授权码");
    }

    /**
     * 单向加密 摘要加密
     *
     * @param in in
     * @return re
     */
    private String getSha1Re(String in) {
        Digester digester = new Digester(DigestAlgorithm.SHA1);
        return digester.digestHex(in);
    }

    /**
     * 获取用户名
     * 不存在获取不到的情况
     *
     * @param authentication 授权
     * @return 用户名
     */
    private UserPO getUserName(String authentication) {
        if (!sharedDataInfo.userAuthenticationMap.containsKey(authentication)) {
            throw new RuntimeException("无效的授权");
        }
        UserAuthenticationInfo info = sharedDataInfo.userAuthenticationMap.get(authentication);
        // 再查一次 确保数据时效性
        UserPO po = userRepository.findById(info.getId()).orElse(null);
        if (null == po) {
            throw new RuntimeException("用户已被禁用或删除");
        }
        return po;
    }

    /**
     * 发送邮件
     *
     * @param email    目标地址
     * @param subject  主题
     * @param content  具体内容
     * @param htmlFlag html 内容
     */
    private void sendEmail(String email, String subject, String content, boolean htmlFlag) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            //标题
            helper.setSubject(subject);
            //内容
            helper.setText(content, htmlFlag);
            //邮件接收者
            helper.setTo(email);
            //邮件发送者，必须和配置文件里的一样，不然授权码匹配不上
            helper.setFrom(myEmail);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("邮件发送失败， {}", e.getMessage(), e.getCause());
        }
    }
}
