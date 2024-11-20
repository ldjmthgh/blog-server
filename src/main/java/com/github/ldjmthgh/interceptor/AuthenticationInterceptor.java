package com.github.ldjmthgh.interceptor;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.github.ldjmthgh.common.CommonConstant;
import com.github.ldjmthgh.model.SharedDataInfo;
import com.github.ldjmthgh.model.UserAuthenticationInfo;
import com.github.ldjmthgh.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: ldj
 * @Date: 2021/10/25 17:23
 */
@Component
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {
    private SharedDataInfo sharedDataInfo;

    @Autowired
    public void getSharedDataInfo(SharedDataInfo sharedDataInfo) {
        this.sharedDataInfo = sharedDataInfo;
    }

    /**
     * 预处理回调方法，实现处理器的预处理
     * 返回值：true表示继续流程；false表示流程中断，不会继续调用其他的拦截器或处理器
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取授权
        String authentication = request.getHeader(CommonConstant.COMMON_STR_AUTHENTICATION);
        if (null == authentication || authentication.isEmpty()) {
            // 返回401
            response.setStatus(401);
            throw new RuntimeException("无效的授权头");
        }
        // 验证授权存在
        if (sharedDataInfo.userAuthenticationMap.containsKey(authentication)) {
            if (sharedDataInfo.userAuthenticationMap.get(authentication).getExpireTime() < System.currentTimeMillis()) {
                sharedDataInfo.userAuthenticationMap.remove(authentication);
                response.setStatus(401);
                throw new RuntimeException("授权头失效，请重新认证");
            }
            return true;
        }
        // 返回401
        response.setStatus(401);
        throw new RuntimeException("无效的授权头，认证失败");
    }

    /**
     * 请求处理完成后
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 获取授权
        String authentication = request.getHeader("Authentication");
        if (null == authentication || authentication.isEmpty()) {
            return;
        }
        // 验证授权是否需要刷新
        if (sharedDataInfo.userAuthenticationMap.containsKey(authentication)
                && sharedDataInfo.userAuthenticationMap.get(authentication).getExpireTime() > System.currentTimeMillis() + CommonConstant.FIVE_MINUTE_SECOND) {
            // 剩余5分钟则刷新token
            UserAuthenticationInfo info = sharedDataInfo.userAuthenticationMap.get(authentication);
            String userName = info.getUserName();
            String newAuthentication = new Digester(DigestAlgorithm.SHA1).digestHex(userName + "_" + IpUtil.getRequestIP(request));
            info.setExpireTime(System.currentTimeMillis() + CommonConstant.THREE_HOUR_SECOND);
            sharedDataInfo.userAuthenticationMap.put(newAuthentication, info);
            sharedDataInfo.userAuthenticationMap.remove(authentication);
            response.setHeader("Authentication", newAuthentication);
        }
    }
}
