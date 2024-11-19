package com.github.ldjmthgh.interceptor;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.ldjws.www.blog.common.CommonStaticValue;
import com.ldjws.www.blog.models.vo.UserVO;
import com.ldjws.www.blog.service.RedisService;
import com.ldjws.www.blog.utils.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    private RedisService redisService;

    @Autowired
    public void getRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 预处理回调方法，实现处理器的预处理
     * 返回值：true表示继续流程；false表示流程中断，不会继续调用其他的拦截器或处理器
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取授权
        String authentication = request.getHeader(CommonStaticValue.COMMON_STR_AUTHENTICATION);
        if (null == authentication || authentication.length() == 0) {
            // 返回401
            response.setStatus(401);
            throw new RuntimeException("无效的授权头");
        }

        // 验证授权是否有效
        if (redisService.hasKey(authentication)) {
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
        if (null == authentication || authentication.length() == 0) {
            return;
        }
        // 验证授权是否需要刷新
        if (redisService.hasKey(authentication)
                && redisService.getExpireTime(authentication) < 360) {
            // 剩余6分钟则刷新token
            String userName = (String) redisService.get(authentication);
            String newAuthentication = new Digester(DigestAlgorithm.SHA1).digestHex(userName + "_" + IpUtil.getRequestIP(request));
            redisService.set(userName, newAuthentication, CommonStaticValue.THREE_HOUR_SECOND);
            redisService.set(newAuthentication, (UserVO) redisService.get(authentication), CommonStaticValue.THREE_HOUR_SECOND);
            redisService.del(authentication);
            response.setHeader("Authentication", newAuthentication);
        }
    }
}
