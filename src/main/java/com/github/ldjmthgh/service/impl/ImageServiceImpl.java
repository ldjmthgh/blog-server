package com.github.ldjmthgh.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.io.FileUtil;
import com.github.ldjmthgh.common.CommonConstant;
import com.github.ldjmthgh.common.CommonResponse;
import com.github.ldjmthgh.common.IPageQueryItem;
import com.github.ldjmthgh.model.dto.ImageDTO;
import com.github.ldjmthgh.model.po.ImagePO;
import com.github.ldjmthgh.model.vo.ImageVO;
import com.github.ldjmthgh.repossitory.ImageRepository;
import com.github.ldjmthgh.service.ImageService;
import com.github.ldjmthgh.utils.ImgUtil;
import com.github.ldjmthgh.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {
    @Value("${project.path.image}")
    String imagePath;

    @Value("${project.path.temp}")
    private String tempPath;

    private ImageRepository imageRepository;

    @Autowired
    public void getImageRepository(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * 上传MD图片流程
     * 1. 前端先选择图片
     * 2. 输入图片信息 并提交
     * 3。两步请求 首先图片信息入库持久化 并返回ID
     * 4. 图片上传 携带ID 视作 更新图片URL路径（图片类型）
     *
     * @param id   ImageId
     * @param file MD 图片
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> upload(Long id, MultipartFile file) {
        ImagePO po = imageRepository.findById(id).orElse(null);
        if (null == po) {
            return CommonResponse.buildUnSuccess("无效的ID参数");
        }
        File imageDir = new File(imagePath);
        if (!imageDir.exists()) {
            if (!imageDir.mkdirs()) {
                imageRepository.deleteById(id);
                return CommonResponse.buildUnSuccess("创建资源文件失败");
            }
        }
        File imageFile = new File(imageDir, UUID.randomUUID().toString() + "-" + file.getOriginalFilename());
        try {
            if (!imageFile.createNewFile()) {
                return CommonResponse.buildUnSuccess("创建资源文件失败");
            }
            FileUtil.writeFromStream(file.getInputStream(), imageFile);
            po.setImageStorageUrl(imageFile.getAbsolutePath());
            po.setImageType(ImgUtil.getImageName(Objects.requireNonNull(file.getOriginalFilename())));
            imageRepository.save(po);
        } catch (Exception e) {
            log.error("转储文件失败, {}", e.getMessage(), e.getCause());
            imageRepository.deleteById(id);
            return CommonResponse.buildUnSuccess("转储文件失败");
        }
        return CommonResponse.buildSuccess();
    }

    /**
     * 新增流程
     * 1. 校验信息 图片名称不允许超过 50个字
     *
     * @param dto 图片数据
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Long> insert(ImageDTO dto) {
        if (dto.getImageName().isEmpty() || dto.getImageName().length() > 50) {
            return CommonResponse.buildUnSuccess("图片名称限定1-50个字符");
        }
        ImagePO old = imageRepository.findByImageName(dto.getImageName());
        if (null != old) {
            return CommonResponse.buildUnSuccess("图片名称重复");
        }
        old = new ImagePO(dto);
        imageRepository.save(old);
        return CommonResponse.buildSuccess(old.getId());
    }

    /**
     * 更新图片名称 50字符
     *
     * @param id        对象ID
     * @param imageName 新图片名称
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> changeImageName(Long id, String imageName) {
        if (imageName.isEmpty() || imageName.length() > 50) {
            return CommonResponse.buildUnSuccess("图片名称限定1-50个字符");
        }
        ImagePO old = imageRepository.findByImageName(imageName);
        if (null != old) {
            return CommonResponse.buildUnSuccess("图片名称重复");
        }
        ImagePO po = imageRepository.findById(id).orElse(null);
        if (null == po) {
            return CommonResponse.buildUnSuccess("无效的ID参数");
        }
        if (Objects.equals(imageName, po.getImageName())) {
            return CommonResponse.buildUnSuccess("无效更新，新旧图片名称一致");
        }
        po.setImageName(imageName);
        imageRepository.save(po);
        return CommonResponse.buildSuccess();
    }

    /**
     * 图片名称是否重复
     *
     * @param imageName 图片名称
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> nameNotRepeat(String imageName) {
        ImagePO old = imageRepository.findByImageName(imageName);
        return CommonResponse.buildSuccess(null == old);
    }

    /**
     * 批量删除
     *
     * @param ids id序列化JSON字符串
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> deleteBatch(String ids) {
        try {
            List<String> idList = CommonConstant.strToStringList(ids);
            if (null == idList || idList.isEmpty()) {
                return CommonResponse.buildSuccess();
            }
            List<Long> idArr = new ArrayList<>(idList.size());
            for (String s : idList) {
                idArr.add(Long.parseLong(s));
            }
            imageRepository.deleteAllById(idArr);
        } catch (Exception e) {
            log.error("批量删除操作异常， {}", e.getMessage(), e.getCause());
            return CommonResponse.buildUnSuccess("批量删除操作异常， " + e.getMessage());
        }
        return CommonResponse.buildSuccess();
    }

    /**
     * 下载图片
     *
     * @param id 图片id
     * @return Base64图片文件
     */
    @Override
    public ResponseEntity<Resource> download(Long id) {
        ImagePO po = imageRepository.findById(id).orElse(null);
        if (null == po) {
            throw new RuntimeException("请求参数异常，参数Id值无效");
        }
        File file = new File(po.getImageStorageUrl());
        if (!file.exists()) {
            throw new RuntimeException("请求资源异常，资源链接已经失效");
        }
        String contentDisposition = ContentDisposition
                .builder("attachment")
                .filename("blog." + po.getImageType())
                .build()
                .toString();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.IMAGE_JPEG)
                .body(new FileSystemResource(po.getImageStorageUrl()));
    }

    /**
     * 后端分页查询
     *
     * @param key 检索Key
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Page<ImageVO>> page(IPageQueryItem key) {
        String orderKey = key.getOrderKey() == null ? "update_time" : key.getOrderKey();
        String searchKey = key.getSearchKey() == null ? "" : key.getSearchKey();
        Pageable pageable = PageRequest.of(key.getIndex(), key.getSize(), Sort.by(orderKey).descending());
        if (null == key.getSearchKey() || key.getSearchKey().isEmpty()) {
            return CommonResponse.buildSuccess(imageRepository.findAll(pageable).map(ImageVO::new));
        }
        return CommonResponse.buildSuccess(imageRepository.findByImageName(searchKey, pageable).map(ImageVO::new));
    }

    /**
     * 获取随机验证码图片
     *
     * @param request 请求
     * @return Base64图片文件
     */
    @Override
    public ResponseEntity<Resource> getCaptchaImg(HttpServletRequest request) {
        // TODO: 验证码禁止频繁刷新事件
        String ip = IpUtil.getRequestIP(request);
        // 封禁ip
        if (null == ip || ip.isEmpty()) {
            throw new RuntimeException("未知的访问来源，禁止请求；请检查代理及其他！");
        }
        //定义图形验证码的长、宽、验证码字符数、干扰元素个数
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 20);
        File tempDir = new File(tempPath);
        if (!tempDir.exists()) {
            if (!tempDir.mkdirs()) {
                // TODO: 返回默认的图片
                return null;
            }
        }
        File imgFile = new File(tempDir, UUID.randomUUID().toString() + ".png");
        //图形验证码写出，可以写出到文件，也可以写出到流
        try {
            if (!imgFile.exists()) {
                if (!imgFile.createNewFile()) {
                    // TODO: 返回默认的图片
                    return null;
                }
            }
            captcha.write(imgFile);
            // todo：ip禁用
        } catch (Exception e) {
            // TODO: 返回默认的图片
            return null;
        }
        String contentDisposition = ContentDisposition
                .builder("attachment")
                .filename(imgFile.getName())
                .build()
                .toString();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.IMAGE_JPEG)
                .body(new FileSystemResource(imgFile.getAbsolutePath()));
    }
}
