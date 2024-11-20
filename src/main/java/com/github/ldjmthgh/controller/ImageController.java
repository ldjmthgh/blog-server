package com.github.ldjmthgh.controller;


import com.github.ldjmthgh.common.CommonResponse;
import com.github.ldjmthgh.common.IPageQueryItem;
import com.github.ldjmthgh.model.dto.ImageDTO;
import com.github.ldjmthgh.model.vo.ImageVO;
import com.github.ldjmthgh.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("image")
public class ImageController {
    private ImageService imageService;

    @Autowired
    public void getImageService(ImageService imageService) {
        this.imageService = imageService;
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
    @PostMapping("/upload")
    public CommonResponse<Boolean> upload(@RequestParam("id") @NotNull(message = "ID参数不允许为空") Long id, @RequestBody MultipartFile file) {
        return imageService.upload(id, file);
    }

    /**
     * 新增流程
     * 1. 校验信息 图片名称不允许超过 26个字
     *
     * @param dto 图片数据
     * @return 三段式返回
     */
    @PostMapping("")
    public CommonResponse<Long> insert(@RequestBody @Valid ImageDTO dto) {
        return imageService.insert(dto);
    }

    /**
     * 更新图片名称 16字符
     *
     * @param id        对象ID
     * @param imageName 新图片名称
     * @return 三段式返回
     */
    @GetMapping("/imageName")
    public CommonResponse<Boolean> changeImageName(@RequestParam("id") @NotNull(message = "ID参数不允许为空") Long id,
                                                   @RequestParam("name") @NotNull(message = "图片名称参数不允许为空") String imageName) {
        return imageService.changeImageName(id, imageName);
    }


    /**
     * 图片名称是否重复
     *
     * @param imageName 图片名称
     * @return 三段式返回
     */
    @GetMapping("/repeat/imageName")
    public CommonResponse<Boolean> nameNotRepeat(@RequestParam("name") @NotNull(message = "图片名称参数不允许为空") String imageName) {
        return imageService.nameNotRepeat(imageName);
    }

    /**
     * 批量删除
     *
     * @param ids id序列化JSON字符串
     * @return 三段式返回
     */
    @PostMapping("/delete")
    public CommonResponse<Boolean> deleteBatch(@RequestParam("ids") @NotNull(message = "ids参数不允许为空") String ids) {
        return imageService.deleteBatch(ids);
    }

    /**
     * 下载图片
     *
     * @param id 图片id
     * @return Base64图片文件
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("id") @NotNull(message = "请求参数id不得为空") Long id) {
        return imageService.download(id);
    }


    /**
     * 后端分页查询
     *
     * @param key 检索Key
     * @return 三段式返回
     */
    @PostMapping("/page")
    public CommonResponse<Page<ImageVO>> page(@RequestBody @Valid IPageQueryItem key) {
        return imageService.page(key);
    }

    /**
     * 获取随机验证码图片
     *
     * @return Base64图片文件
     */
    @GetMapping("/captcha/resource")
    public ResponseEntity<Resource> getCaptchaImg(HttpServletRequest request) {
        return imageService.getCaptchaImg(request);
    }
}
