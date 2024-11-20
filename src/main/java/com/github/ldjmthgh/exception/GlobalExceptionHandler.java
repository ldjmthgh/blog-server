package com.github.ldjmthgh.exception;

import com.github.ldjmthgh.common.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: ldj
 * @Date: 2020/12/8 20:52
 */

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理博客自定义的异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = CommonExceptionHandler.class)
    @ResponseBody
    public CommonResponse<String> exceptionHandler(HttpServletRequest req, CommonExceptionHandler e) {
        logger.error("发生异常！原因是: {}", e.getMessage(), e.getCause());
        return CommonResponse.buildUnSuccess(e.getErrorMsg());
    }

    /**
     * 处理博客Bean校验的异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResponse<String> exceptionHandler(HttpServletRequest req, BindException e) {
        List<FieldError> fieldErrorList = e.getBindingResult().getFieldErrors();
        if (null != fieldErrorList && fieldErrorList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (FieldError fieldError : fieldErrorList) {
                logger.error("发生参数校验异常！原因是: {}", fieldError.getDefaultMessage());
                sb.append(fieldError.getDefaultMessage()).append("\r\n");
            }
            return CommonResponse.buildUnSuccess(sb.toString());
        }
        logger.error("发生异常！原因是: {}", e.getMessage(), e.getCause());
        return CommonResponse.buildUnSuccess(e.getMessage());

    }

    /**
     * 处理空指针的异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public CommonResponse<String> exceptionHandler(HttpServletRequest req, NullPointerException e) {
        logger.error("发生空指针异常！原因是: {}", e.getMessage(), e.getCause());
        return CommonResponse.buildUnSuccess(e.getMessage());
    }

    /**
     * 处理其他异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResponse<String> exceptionHandler(HttpServletRequest req, Exception e) {
        logger.error("未知异常！原因是: {}", e.getMessage(), e.getCause());
        return CommonResponse.buildUnSuccess(e.getMessage());
    }
}
