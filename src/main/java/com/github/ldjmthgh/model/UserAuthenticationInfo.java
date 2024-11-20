package com.github.ldjmthgh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author: ldj
 * @Date: 2024/11/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserAuthenticationInfo {
    /**
     * 用户Id
     */
    private Long id;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 授权信息
     */
    private String authentication;
    /**
     * 过期时间
     */
    private Long expireTime;
    /**
     * 用户昵称
     */
    private String nickName;
}
