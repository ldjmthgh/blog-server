package com.github.ldjmthgh.exception;


/**
 * @Author: ldj
 * @Date: 2020/12/8 21:09
 */
public class CommonExceptionHandler extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected int errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;

    public CommonExceptionHandler() {
        super();
    }

    public CommonExceptionHandler(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public CommonExceptionHandler(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public CommonExceptionHandler(int errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public CommonExceptionHandler(String errorMsg, Throwable cause, String detailMsg) {
        super(errorMsg, cause);
        this.errorMsg = errorMsg + "; Detail: \t" + detailMsg;
    }


    public int getErrorCode() {
        return errorCode;
    }

    public CommonExceptionHandler setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public CommonExceptionHandler setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
