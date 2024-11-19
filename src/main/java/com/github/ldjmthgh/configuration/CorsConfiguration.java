package com.github.ldjmthgh.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @Author: ldj
 * @Date: 2020/2/24 9:36
 * 开启跨域请求
 * 1.允许任何域名
 * 2.允许任何头
 * 3.允许任何方法
 */

@Configuration
public class CorsConfiguration {

    /**
     * 开启跨域请求
     * 1.允许任何域名使用
     * 2.允许任何头
     * 3.允许任何方法（post、get等）
     */
    private org.springframework.web.cors.CorsConfiguration buildConfig(){
        org.springframework.web.cors.CorsConfiguration corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
        // 允许头部设置
        corsConfiguration.addAllowedHeader("*");
        // 允许的方法
        corsConfiguration.addAllowedMethod(HttpMethod.GET);
        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
        corsConfiguration.addAllowedMethod(HttpMethod.POST);
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
        // 允许跨域访问的源
        corsConfiguration.addAllowedOrigin("*");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
