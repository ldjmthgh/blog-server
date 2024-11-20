package com.github.ldjmthgh.service;


import com.github.ldjmthgh.common.CommonResponse;
import com.github.ldjmthgh.common.IPageQueryItem;
import com.github.ldjmthgh.model.dto.ArticleDTO;
import com.github.ldjmthgh.model.vo.ArticleVO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ArticleService {
    /**
     * 上传MD文件流程
     * 1. 前端先选择文件
     * 2. 输入文件信息 并提交
     * 3。两步请求 首先文件信息入库持久化 并返回ID
     * 4. 文件上传 携带ID 视作 更新文件URL路径（文件类型）
     *
     * @param id   ArticleId
     * @param file MD 文件
     * @return 三段式返回
     */
    CommonResponse<Boolean> upload(Long id, MultipartFile file) throws IOException;

    /**
     * 新增流程
     * 1. 校验信息 文件名称不允许超过 26个字
     *
     * @param dto 文件数据
     * @return 三段式返回
     */
    CommonResponse<Long> insert(ArticleDTO dto);

    /**
     * 更新MD 文件
     *
     * @param id  ArticleId
     * @param dto 新文件数据
     * @return 三段式返回
     */
    CommonResponse<Boolean> update(Long id, ArticleDTO dto);

    /**
     * 文件名称是否重复
     *
     * @param articleName 文件名称
     * @return 三段式返回
     */
    CommonResponse<Boolean> nameNotRepeat(String articleName);

    /**
     * 检索
     *
     * @param id 文档ID
     * @return 三段式返回
     */
    CommonResponse<ArticleVO> select(Long id);

    /**
     * 发布 取消发布
     *
     * @param id 文档ID
     * @return 三段式返回
     */
    CommonResponse<Boolean> publish(Long id);

    /**
     * 获取文档内容
     *
     * @param id 文档ID
     * @return 三段式返回
     */
    CommonResponse<String> getMdArticleContent(Long id);


    /**
     * 获取文档内容
     *
     * @param id 文档ID
     * @return 三段式返回
     */
    CommonResponse<String> getPublishedMdArticleContent(Long id);

    /**
     * 更新文档内容
     *
     * @param id      文档ID
     * @param content 文档新内容
     * @return 三段式返回
     */
    CommonResponse<Boolean> updateMdArticleContent(Long id, String content);


    /**
     * 批量删除
     *
     * @param ids id序列化JSON字符串
     * @return 三段式返回
     */
    CommonResponse<Boolean> deleteBatch(String ids);


    /**
     * 后端分页查询
     *
     * @param key 检索Key
     * @return 三段式返回
     */
    CommonResponse<Page<ArticleVO>> page(IPageQueryItem key);

    /**
     * 后端分页查询
     *
     * @param key 检索Key
     * @return 三段式返回
     */
    CommonResponse<Page<ArticleVO>> publishedPage(IPageQueryItem key);

}
