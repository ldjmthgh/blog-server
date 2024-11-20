package com.github.ldjmthgh.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ArticleDTO {
    /**
     * 限定38个字
     */
    private String articleName;
    /**
     * 文章简述 可为空 限长255
     */
    private String articleSummary;
    /**
     * true ?  发布 : 草稿
     */
    private Boolean published;
}
