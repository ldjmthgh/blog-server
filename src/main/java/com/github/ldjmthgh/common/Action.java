package com.github.ldjmthgh.common;

/**
 * @Author: ldj
 * @Date: 2020/7/27 10:44
 */
@FunctionalInterface
public interface Action<T> {
    /**
     * 默认操作
     * @param t t
     */
    void apply(T t);
}
