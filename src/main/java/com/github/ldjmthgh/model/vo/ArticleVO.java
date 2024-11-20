package com.github.ldjmthgh.model.vo;


import com.github.ldjmthgh.model.po.ArticlePO;
import com.github.ldjmthgh.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ArticleVO {
    /**
     * 文章ID dto vo 均需要 缩略图、详情查看
     */
    private Long id;
    /**
     * 限定50个字
     */
    private String articleName;
    /**
     * 文章简述 可为空
     */
    private String articleSummary;
    /**
     * true ?  发布 : 草稿
     */
    private Boolean published;
    /**
     * 创建时间
     * 样式 yyyy:mm:dd hh:ss
     */
    private String createTime;
    /**
     * 最近一次更新时间
     */
    private String updateTime;

    public ArticleVO(ArticlePO po) {
        this.id = po.getId();
        this.articleName = po.getArticleName();
        this.articleSummary = po.getArticleSummary();
        this.published = null != po.getPublished() && po.getPublished() > 0;
        this.createTime = DateUtil.simpleDateFormat(po.getCreateTime());
        this.updateTime = DateUtil.simpleDateFormat(po.getUpdateTime());
    }
}
