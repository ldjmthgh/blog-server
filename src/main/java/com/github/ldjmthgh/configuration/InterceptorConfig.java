package com.github.ldjmthgh.configuration;

import com.github.ldjmthgh.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ldj
 * @Date: 2021/10/15 16:07
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private AuthenticationInterceptor authenticationInterceptor;


    @Autowired
    public void getAuthenticationInterceptor(AuthenticationInterceptor authenticationInterceptor) {
        this.authenticationInterceptor = authenticationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链 先写放行后写拦截
        // addPathPatterns 用于添加拦截规则，/**表示拦截所有请求 放到后面
        // excludePathPatterns 用户排除拦截
        List<String> excludePathList = new ArrayList<>();
        // 判断是否初始化接口
        excludePathList.add("/user/init");
        // 登录
        excludePathList.add("/user/login");
        // 注册
        excludePathList.add("/user/register");
        // 邮箱重置密码
        excludePathList.add("/email/reset");
        // 头像下载
        excludePathList.add("/user/download");
        // 授权认证 防止异常返回 做到用户无感
        excludePathList.add("/user/check/authentication");
        // MD 图片下载
        excludePathList.add("/image/download");
        // article public 操作 下载 检索
        excludePathList.add("/article/public/page");
        excludePathList.add("/article/public/content");


        // 先拦截
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePathList);
    }


}
