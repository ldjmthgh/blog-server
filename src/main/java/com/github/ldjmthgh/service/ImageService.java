package com.github.ldjmthgh.service;

import com.github.ldjmthgh.common.CommonResponse;
import com.github.ldjmthgh.common.IPageQueryItem;
import com.github.ldjmthgh.model.dto.ImageDTO;
import com.github.ldjmthgh.model.vo.ImageVO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface ImageService {
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
    CommonResponse<Boolean> upload(Long id, MultipartFile file);

    /**
     * 新增流程
     * 1. 校验信息 图片名称不允许超过 26个字
     *
     * @param dto 图片数据
     * @return 三段式返回
     */
    CommonResponse<Long> insert(ImageDTO dto);

    /**
     * 更新图片名称 59字符
     *
     * @param id        对象ID
     * @param imageName 新图片名称
     * @return 三段式返回
     */
    CommonResponse<Boolean> changeImageName(Long id, String imageName);

    /**
     * 图片名称是否重复
     *
     * @param imageName 图片名称
     * @return 三段式返回
     */
    CommonResponse<Boolean> nameNotRepeat(String imageName);

    /**
     * 批量删除
     *
     * @param ids id序列化JSON字符串
     * @return 三段式返回
     */
    CommonResponse<Boolean> deleteBatch(String ids);


    /**
     * 下载图片
     *
     * @param id 图片id
     * @return Base64图片文件
     */
    ResponseEntity<Resource> download(String id);

    /**
     * 后端分页查询
     *
     * @param key 检索Key
     * @return 三段式返回
     */
    CommonResponse<Page<ImageVO>> page(IPageQueryItem key);

    /**
     * 获取随机验证码图片
     *
     * @param request 请求
     * @return Base64图片文件
     */
    ResponseEntity<Resource> getCaptchaImg(HttpServletRequest request);
}
