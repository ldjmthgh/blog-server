package com.github.ldjmthgh.common;

/**
 * @Author: ldj
 * @Date: 2020/7/24 15:30
 */
public class CommonResponse<T> {
    /**
     * 状态码
     * 1 正整数 本次请求成功且接口功能正常
     * 0 本次请求正在处理中
     * -1 负整数 本次请求成功但是接口功能异常 具体异常请查看msg
     */
    private Integer code;

    /**
     * 具体信息描述
     * code 1 状态下 一般为 操作成功提示
     * code -1 状态下 一般为 异常描述
     */
    private String msg;
    /**
     * 请求返回携带的结果数据
     */
    private T data;

    public CommonResponse() {
    }

    public CommonResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public CommonResponse<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public CommonResponse<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public CommonResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 返回正确标识，无数据
     *
     * @return 正确
     */
    public static <T> CommonResponse<T> buildSuccess() {
        return new CommonResponse<>(1, null, null);
    }

    /**
     * 返回正确标识，含数据
     *
     * @return 正确
     */
    public static <T> CommonResponse<T> buildSuccess(T data) {
        return new CommonResponse<>(1, null, data);
    }

    /**
     * 返回异常标识，无数据，含异常描述
     *
     * @return 异常
     */
    public static <T> CommonResponse<T> buildUnSuccess(String msg) {
        return new CommonResponse<>(-1, msg, null);
    }

    /**
     * 返回 一般构建, 无数据
     *
     * @return 一般
     */
    public static <T> CommonResponse<T> buildCommon(String msg, Integer code) {
        return new CommonResponse<>(code, msg, null);
    }

    /**
     * 返回 一般构建, 含数据
     *
     * @return 一般
     */
    public static <T> CommonResponse<T> buildCommon(String msg, Integer code, T data) {
        return new CommonResponse<>(code, msg, data);
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
