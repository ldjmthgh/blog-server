package com.github.ldjmthgh.service;

import com.github.ldjmthgh.common.CommonResponse;
import com.github.ldjmthgh.model.dto.UserDTO;
import com.github.ldjmthgh.model.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    /**
     * @param dto 用户数据
     * @return 注册
     */
    CommonResponse<Boolean> register(UserDTO dto);

    /**
     * @param response 返回
     * @param dto      用户数据
     * @return 三段式返回
     */
    CommonResponse<String> login(HttpServletResponse response, UserDTO dto);

    /**
     * @param authentication 授权
     * @return 三段式返回
     */
    CommonResponse<Boolean> logout(String authentication);

    /**
     * 获取用户信息
     *
     * @param authentication 授权头
     * @return 三段式返回
     */
    CommonResponse<UserVO> getUserInfo(String authentication);

    /**
     * @param authentication 授权
     * @param file           头像文件
     * @return 三段式返回
     */
    CommonResponse<Boolean> changeAvatar(String authentication, MultipartFile file);

    /**
     * @param authentication 授权
     * @param userName       用户名
     * @return 三段式返回
     */
    CommonResponse<Boolean> changeUserName(String authentication, String userName);

    /**
     * @param authentication 授权
     * @param nickName       昵称
     * @return 三段式返回
     */
    CommonResponse<Boolean> changeNickName(String authentication, String nickName);

    /**
     * @param authentication 授权
     * @param oldPwd         原密码
     * @param newPwd         新密码
     * @return 三段式返回
     */
    CommonResponse<Boolean> resetPassword(String authentication, String oldPwd, String newPwd);


    /**
     * 发送重置密码
     *
     * @param authentication    验证
     * @param email   邮件
     * @param request 请求
     * @return 返回
     */
    CommonResponse<Boolean> sendEmailRestPwd(String email, String authentication, HttpServletRequest request);

    /**
     * 判断是否存在用户 存在及默认为已初始化
     *
     * @return 三段式返回
     */
    CommonResponse<Boolean> init();

    /**
     * 验证授权是否有效
     *
     * @param authentication 授权码
     * @return 三段式
     */
    CommonResponse<Boolean> checkAuthentication(String authentication);
}
