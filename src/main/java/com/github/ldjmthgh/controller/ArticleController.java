package com.github.ldjmthgh.controller;

import com.github.ldjmthgh.common.CommonResponse;
import com.github.ldjmthgh.common.IPageQueryItem;
import com.github.ldjmthgh.model.dto.ArticleDTO;
import com.github.ldjmthgh.model.vo.ArticleVO;
import com.github.ldjmthgh.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@RequestMapping("article")
public class ArticleController {
    private ArticleService articleService;

    @Autowired
    public void getArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 上传MD文档流程
     * 1. 前端先选择文档
     * 2. 输入文档信息 并提交
     * 3。两步请求 首先文档信息入库持久化 并返回ID
     * 4. 文档上传 携带ID 视作 更新文档URL路径（文档类型）
     *
     * @param id   ImageId
     * @param file MD 文档
     * @return 三段式返回
     */
    @PostMapping("/upload")
    public CommonResponse<Boolean> upload(@RequestParam("id") @NotNull(message = "ID参数不允许为空") Long id, @RequestBody MultipartFile file) throws IOException {
        return articleService.upload(id, file);
    }

    /**
     * 新增流程
     * 1. 校验信息 文档名称不允许超过 26个字
     *
     * @param dto 文档数据
     * @return 三段式返回
     */
    @PostMapping("")
    public CommonResponse<Long> insert(@RequestBody @Valid ArticleDTO dto) {
        return articleService.insert(dto);
    }

    /**
     * 更新文档简述  简述限制在28字符
     *
     * @param dto 文档数据
     * @return 三段式返回
     */
    @PutMapping("")
    public CommonResponse<Boolean> update(@RequestParam("id") @NotNull(message = "ID参数不允许为空") Long id,
                                          @RequestBody @Valid ArticleDTO dto) {
        return articleService.update(id, dto);
    }

    /**
     * 检索
     *
     * @param id 文档ID
     * @return 三段式返回
     */
    @GetMapping("")
    public CommonResponse<ArticleVO> select(@RequestParam("id") @NotNull(message = "文档ID参数不允许为空") Long id) {
        return articleService.select(id);
    }

    /**
     * 发布 取消发布
     *
     * @param id 文档ID
     * @return 三段式返回
     */
    @GetMapping("/publish")
    public CommonResponse<Boolean> publish(@RequestParam("id") @NotNull(message = "文档ID参数不允许为空") Long id) {
        return articleService.publish(id);
    }

    /**
     * 获取文档内容
     *
     * @param id 文档ID
     * @return 三段式返回
     */
    @GetMapping("/content")
    public CommonResponse<String> getMdArticleContent(@RequestParam("id") @NotNull(message = "文档ID参数不允许为空") Long id) {
        return articleService.getMdArticleContent(id);
    }


    /**
     * 获取文档内容
     *
     * @param id 文档ID
     * @return 三段式返回
     */
    @GetMapping("/public/content")
    public CommonResponse<String> getPublishedMdArticleContent(@RequestParam("id") @NotNull(message = "文档ID参数不允许为空") Long id) {
        return articleService.getPublishedMdArticleContent(id);
    }

    /**
     * 更新文档内容
     *
     * @param id      文档ID
     * @param content 文档新内容
     * @return 三段式返回
     */
    @PostMapping("/update/content")
    public CommonResponse<Boolean> updateMdArticleContent(@RequestParam("id") @NotNull(message = "文档ID参数不允许为空") Long id,
                                                          @RequestParam("content") String content) {
        return articleService.updateMdArticleContent(id, content);
    }

    /**
     * 文档名称是否重复
     *
     * @param articleName 文档名称
     * @return 三段式返回
     */
    @GetMapping("/repeat/articleName")
    public CommonResponse<Boolean> nameNotRepeat(@RequestParam("name") @NotNull(message = "文档名称参数不允许为空") String articleName) {
        return articleService.nameNotRepeat(articleName);
    }

    /**
     * 批量删除
     *
     * @param ids id序列化JSON字符串
     * @return 三段式返回
     */
    @PostMapping("/delete")
    public CommonResponse<Boolean> deleteBatch(@RequestParam("ids") @NotNull(message = "ids参数不允许为空") String ids) {
        return articleService.deleteBatch(ids);
    }


    /**
     * 后端分页查询
     *
     * @param key 检索Key
     * @return 三段式返回
     */
    @PostMapping("/page")
    public CommonResponse<Page<ArticleVO>> page(@RequestBody @Valid IPageQueryItem key) {
        return articleService.page(key);
    }

    /**
     * 后端分页查询
     *
     * @param key 检索Key
     * @return 三段式返回
     */
    @PostMapping("/public/page")
    public CommonResponse<Page<ArticleVO>> publishedPage(@RequestBody @Valid IPageQueryItem key) {
        return articleService.publishedPage(key);
    }
}
