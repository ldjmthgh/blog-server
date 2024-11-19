package com.github.ldjmthgh.common;

import javax.validation.constraints.NotNull;

/**
 * @Author: ldj
 * @Date: 2020/12/28 21:26
 */
public class IPageQueryItem {
    /**
     * 开始的序列 默认0  页数
     */
    @NotNull(message = "分页起始序列不可为空")
    private Integer startIndex;

    /**
     * 分页存储的数量
     */
    @NotNull(message = "分页数据每页存储条目不可为空")
    private Long size;

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

    public IPageQueryItem() {
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public IPageQueryItem setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public IPageQueryItem setSize(Long size) {
        this.size = size;
        return this;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public IPageQueryItem setOrderKey(String orderKey) {
        this.orderKey = orderKey;
        return this;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public IPageQueryItem setSearchKey(String searchKey) {
        this.searchKey = searchKey;
        return this;
    }

    public String getFilterKey() {
        return filterKey;
    }

    public IPageQueryItem setFilterKey(String filterKey) {
        this.filterKey = filterKey;
        return this;
    }

    @Override
    public String toString() {
        return "QueryPageKey{" +
                "startIndex=" + startIndex +
                ", size=" + size +
                ", orderKey='" + orderKey + '\'' +
                ", searchKey='" + searchKey + '\'' +
                '}';
    }
}
