package com.github.ldjmthgh.common;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: ldj
 * @Date: 2020/12/28 21:26
 */
@Data
public class IPageQueryItem {
    /**
     * 开始的序列 默认0  页数
     */
    @NotNull(message = "第几页")
    private Integer index;

    /**
     * 分页存储的数量
     */
    @NotNull(message = "分页数据每页存储条目不可为空")
    private Integer size;

    /**
     * 排序key
     */
    private String orderKey;

    /**
     * 自定义检索key
     */
    private String searchKey;

    /**
     * 预留检索Key
     */
    private String filterKey;
}
