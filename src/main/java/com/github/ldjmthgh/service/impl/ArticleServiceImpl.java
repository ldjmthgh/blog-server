package com.github.ldjmthgh.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.github.ldjmthgh.common.CommonConstant;
import com.github.ldjmthgh.common.CommonResponse;
import com.github.ldjmthgh.common.IPageQueryItem;
import com.github.ldjmthgh.model.dto.ArticleDTO;
import com.github.ldjmthgh.model.po.ArticlePO;
import com.github.ldjmthgh.model.vo.ArticleVO;
import com.github.ldjmthgh.repossitory.ArticleRepository;
import com.github.ldjmthgh.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    @Value("${project.path.article}")
    String articlePath;

    private ArticleRepository articleRepository;

    @Autowired
    public void getArticleRepository(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 上传MD文件流程
     * 1. 前端先选择文件
     * 2. 输入文件信息 并提交
     * 3。两步请求 首先文件信息入库持久化 并返回ID
     * 4. 文件上传 携带ID 视作 更新文件URL路径（文件类型）
     *
     * @param id   ImageId
     * @param file MD 文件
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> upload(Long id, MultipartFile file) throws IOException {
        return this.upload(id, file.getInputStream(), file.getOriginalFilename());
    }

    private CommonResponse<Boolean> upload(Long id, InputStream in, String originFileName) {
        ArticlePO po = articleRepository.findById(id).orElse(null);
        if (null == po) {
            articleRepository.deleteById(id);
            return CommonResponse.buildUnSuccess("无效的ID参数");
        }

        File articleDir = new File(articlePath);
        if (!articleDir.exists()) {
            if (!articleDir.mkdirs()) {
                articleRepository.deleteById(id);
                return CommonResponse.buildUnSuccess("创建资源文件失败");
            }
        }
        File articleFile = new File(articleDir, UUID.randomUUID().toString() + "-" + originFileName);
        try {
            if (!articleFile.createNewFile()) {
                articleRepository.deleteById(id);
                return CommonResponse.buildUnSuccess("创建资源文件失败");
            }
            FileUtil.writeFromStream(in, articleFile);
            po.setArticleStorageUrl(articleFile.getAbsolutePath());
            articleRepository.save(po);
        } catch (Exception e) {
            log.error("转储文件失败, {}", e.getMessage(), e.getCause());
            articleRepository.deleteById(id);
            return CommonResponse.buildUnSuccess("转储文件失败");
        }
        return CommonResponse.buildSuccess();
    }

    /**
     * 新增流程
     * 1. 校验信息 文件名称不允许超过 26个字
     *
     * @param dto 文件数据
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Long> insert(ArticleDTO dto) {
        if (dto.getArticleName().isEmpty() || dto.getArticleName().length() > 38) {
            return CommonResponse.buildUnSuccess("文章名称限定1-38个字符");
        }
        if (null != dto.getArticleSummary() && dto.getArticleSummary().length() > 255) {
            return CommonResponse.buildUnSuccess("文章简述限定0-255个字符");
        }
        if (null == dto.getPublished()) {
            dto.setPublished(false);
        }
        ArticlePO old = articleRepository.findByArticleName(dto.getArticleName());
        if (null != old) {
            return CommonResponse.buildUnSuccess("文章名称重复");
        }
        old = new ArticlePO(dto);
        articleRepository.save(old);
        return CommonResponse.buildSuccess(old.getId());
    }

    /**
     * 更新MD 文件数据
     *
     * @param dto 新文件数据
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> update(Long id, ArticleDTO dto) {
        ArticlePO old = articleRepository.findById(id).orElse(null);
        boolean flag = false;
        if (old != null && !Objects.equals(old.getArticleName(), dto.getArticleName())) {
            if (dto.getArticleName().isEmpty() || dto.getArticleName().length() > 38) {
                return CommonResponse.buildUnSuccess("文章名称限定1-38个字");
            }
            if (this.nameNotRepeat(dto.getArticleName()).getData()) {
                old.setArticleName(dto.getArticleName());
                flag = true;
            } else {
                return CommonResponse.buildUnSuccess("文章名称重复");
            }
        }

        if (old != null && !Objects.equals(old.getArticleSummary(), dto.getArticleSummary())) {
            old.setArticleSummary(dto.getArticleSummary());
            flag = true;
        }
        if (old != null && !Objects.equals(old.getPublished() > 0, dto.getPublished())) {
            old.setPublished(dto.getPublished() ? 1 : -1);
            flag = true;
        }
        if (flag) {
            articleRepository.save(old);
        }
        return CommonResponse.buildSuccess();
    }

    /**
     * 文件名称是否重复
     *
     * @param articleName 文件名称
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> nameNotRepeat(String articleName) {
        ArticlePO old = articleRepository.findByArticleName(articleName);
        return CommonResponse.buildSuccess(null == old);
    }

    /**
     * 检索
     *
     * @param id 文档ID
     * @return 三段式返回
     */
    @Override
    public CommonResponse<ArticleVO> select(Long id) {
        ArticlePO po = articleRepository.findById(id).orElse(null);
        if (null == po) {
            return CommonResponse.buildUnSuccess("无效的Id参数");
        }
        return CommonResponse.buildSuccess(new ArticleVO(po));
    }

    @Override
    public CommonResponse<Boolean> publish(Long id) {
        ArticlePO po = articleRepository.findById(id).orElse(null);
        if (null == po) {
            return CommonResponse.buildUnSuccess("无效的Id参数");
        }
        if (po.getPublished() > 0) {
            po.setPublished(-1);
        } else {
            po.setPublished(1);
        }
        articleRepository.save(po);
        return CommonResponse.buildSuccess();
    }

    /**
     * 获取文档内容
     *
     * @param id 文档ID
     * @return 三段式返回
     */
    @Override
    public CommonResponse<String> getMdArticleContent(Long id) {
        ArticlePO po = articleRepository.findById(id).orElse(null);
        if (null == po) {
            return CommonResponse.buildUnSuccess("无效的Id参数");
        }
        File file = new File(po.getArticleStorageUrl());
        if (!file.exists() || file.isDirectory()) {
            return CommonResponse.buildUnSuccess("资源文件已损坏");
        }
        String content = null;
        try {
            content = IoUtil.read(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("读取本地资源失败， {}", e.getMessage(), e.getCause());
            return CommonResponse.buildUnSuccess("读取本地资源失败");
        }
        return CommonResponse.buildSuccess(content);
    }

    @Override
    public CommonResponse<String> getPublishedMdArticleContent(Long id) {
        ArticlePO po = articleRepository.findById(id).orElse(null);
        if (null == po) {
            return CommonResponse.buildUnSuccess("无效的Id参数");
        }
        if (po.getPublished() <= 0 || po.getRemoved() > 0) {
            return CommonResponse.buildUnSuccess("无效的Id参数");
        }
        File file = new File(po.getArticleStorageUrl());
        if (!file.exists() || file.isDirectory()) {
            return CommonResponse.buildUnSuccess("资源文件已损坏");
        }
        String content = null;
        try {
            content = IoUtil.read(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("读取本地资源失败， {}", e.getMessage(), e.getCause());
            return CommonResponse.buildUnSuccess("读取本地资源失败");
        }
        return CommonResponse.buildSuccess(content);
    }

    /**
     * 更新文档内容
     *
     * @param id      文档ID
     * @param content 文档新内容
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Boolean> updateMdArticleContent(Long id, String content) {
        ArticlePO po = articleRepository.findById(id).orElse(null);
        if (null == po) {
            return CommonResponse.buildUnSuccess("无效的Id参数");
        }
        File file = new File(po.getArticleStorageUrl());
        if (file.isDirectory()) {
            return CommonResponse.buildUnSuccess("目标资源文件异常");
        }

        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return CommonResponse.buildUnSuccess("创建资源文件失败");
                }
            }
            FileUtil.writeString(content, file, StandardCharsets.UTF_8);
            articleRepository.save(po);
        } catch (Exception e) {
            log.error("文件写入失败, {}", e.getMessage(), e.getCause());
            return CommonResponse.buildUnSuccess("本地文件资源文件写入失败");
        }
        return CommonResponse.buildSuccess();
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
            articleRepository.deleteAllById(idArr);
        } catch (Exception e) {
            log.error("批量删除操作异常， {}", e.getMessage(), e.getCause());
            return CommonResponse.buildUnSuccess("批量删除操作异常， " + e.getMessage());
        }
        return CommonResponse.buildSuccess();
    }

    /**
     * 后端分页查询
     *
     * @param key 检索Key
     * @return 三段式返回
     */
    @Override
    public CommonResponse<Page<ArticleVO>> page(IPageQueryItem key) {
        String orderKey = key.getOrderKey() == null ? "update_time" : key.getOrderKey();
        String searchKey = key.getSearchKey() == null ? "" : key.getSearchKey();
        Pageable pageable = PageRequest.of(key.getIndex(), key.getSize(), Sort.by(orderKey).descending());
        if (null == key.getSearchKey() || key.getSearchKey().isEmpty()) {
            return CommonResponse.buildSuccess(articleRepository.findAll(pageable).map(ArticleVO::new));
        }
        return CommonResponse.buildSuccess(articleRepository.findByArticleName(searchKey, pageable).map(ArticleVO::new));
    }

    @Override
    public CommonResponse<Page<ArticleVO>> publishedPage(IPageQueryItem key) {
        String orderKey = key.getOrderKey() == null ? "update_time" : key.getOrderKey();
        String searchKey = key.getSearchKey() == null ? "" : key.getSearchKey();
        Pageable pageable = PageRequest.of(key.getIndex(), key.getSize(), Sort.by(orderKey).descending());
        if (null == key.getSearchKey() || key.getSearchKey().isEmpty()) {
            return CommonResponse.buildSuccess(articleRepository.findByPublished(1, pageable).map(ArticleVO::new));
        }
        return CommonResponse.buildSuccess(articleRepository.findByArticleNameAndPublished(searchKey, 1, pageable).map(ArticleVO::new));
    }
}
