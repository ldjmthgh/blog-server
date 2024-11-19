package com.github.ldjmthgh.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.github.ldjmthgh.common.CommonResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: ldj
 * @Date: 2020/12/14 21:59
 */
@Aspect
@Component
public class LogAspectConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(LogAspectConfiguration.class);

    /**
     * ..表示包及子包 该方法代表controller层的所有方法
     */
    @Pointcut("execution(public * com.github.ldjmthgh.controller..*.*(..))")
    public void controllerMethod() {
    }

    /**
     * 方法执行前
     *
     * @param joinPoint
     */
    @Before("controllerMethod()")
    public void logRequestInfo(JoinPoint joinPoint) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (null == attributes || null == attributes.getRequest()) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();
            StringBuilder requestLog = new StringBuilder();
            Signature signature = joinPoint.getSignature();
            requestLog.append("请求信息：").append("URL = {").append(request.getRequestURI()).append("},\t")
                    .append("请求方式 = {").append(request.getMethod()).append("},\t")
                    .append("请求IP = {").append(request.getRemoteAddr()).append("},\t")
                    .append("应答方法 = {").append(signature.getDeclaringTypeName()).append(".")
                    .append(signature.getName()).append("},\t");

            // 处理请求参数
            String[] paramNames = ((MethodSignature) signature).getParameterNames();
            Object[] paramValues = joinPoint.getArgs();
            int paramLength = null == paramNames ? 0 : paramNames.length;
            if (paramLength == 0) {
                requestLog.append("请求参数 = {} ");
            } else {
                requestLog.append("请求参数 = [");
                for (int i = 0; i < paramLength; i++) {
                    try {
                        requestLog.append(paramNames[i]).append("=").append(
                                new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(paramValues[i]));
                        if (i != paramLength - 1) {
                            requestLog.append(",");
                        } else {
                            requestLog.append("]");
                        }
                    } catch (InvalidDefinitionException e) {
                        logger.error("序列化请求参数{}失败，参数类型 {}", paramNames[i], paramValues[i].getClass());
                        logger.error(e.getMessage(), e.getCause());
                    }
                }
            }
            logger.info(requestLog.toString());
        } catch (Exception e) {
            logger.error("切面日志 请求解析异常, 详细： {}", e.getMessage());
        }
    }

    /**
     * 方法执行后
     *
     * @param commonResponse re
     */
    @AfterReturning(returning = "commonResponse", pointcut = "controllerMethod()")
    public void logResultInfo(CommonResponse<?> commonResponse) {
        try {
            logger.info("请求返回码 {}", commonResponse.getCode());
            logger.info("请求返回信息 {}", commonResponse.getMsg());
            logger.info("请求返回结果 {}", commonResponse.getData());
        } catch (Exception e) {
            logger.warn("切面日志 请求结果解析异常, 详细为： {}", e.getMessage());
        }

    }

}
