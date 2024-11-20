package com.github.ldjmthgh.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: ldj
 * @Date: 2024/11/20
 */
public class SharedDataInfo {
    // 鉴权token， 用户信息
    public Map<String, UserAuthenticationInfo> userAuthenticationMap;

    public SharedDataInfo() {
        this.userAuthenticationMap = new ConcurrentHashMap<>();
    }
}
