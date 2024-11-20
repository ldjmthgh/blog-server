package com.github.ldjmthgh.model.po;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "article_info")
public class ArticlePO {
    /**
     * 文章ID dto vo 均需要 缩略图、详情查看
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文章名称 限定50个字
     */
    @Column(name = "article_name",length = 50)
    private String articleName;

    /**
     * 文章简述 可为空
     */
    @Column(name = "article_summary", length = 500)
    private String articleSummary;


    /**
     * 文章简述 可为空
     */
    @Lob
    @Column(name = "article_content")
    private String articleContent;

    /**
     * MD文档存储的绝对路径  .../articleId.md
     */
    @Column(name = "article_storage_url",length = 500)
    @JsonIgnore
    private String articleStorageUrl;

    /**
     * true ?  发布 : 草稿     正数为true
     */
    @Column(name = "published")
    private Integer published;

    /**
     * true ?  回收站 : 使用中  正数为true
     */
    @Column(name = "removed")
    private Integer removed;

    /**
     * 创建时间
     * 样式 yyyy:mm:dd hh:ss
     */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    /**
     * 最近一次更新时间
     */
    @LastModifiedDate
    @Column(name = "update_time")
    private Date updateTime;
}
