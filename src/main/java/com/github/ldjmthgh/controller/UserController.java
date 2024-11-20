package com.github.ldjmthgh.controller;

import com.github.ldjmthgh.common.CommonResponse;
import com.github.ldjmthgh.common.InsertAction;
import com.github.ldjmthgh.common.LoginAction;
import com.github.ldjmthgh.model.dto.UserDTO;
import com.github.ldjmthgh.model.vo.UserVO;
import com.github.ldjmthgh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    @Autowired
    public void getUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册流程
     * 1. 获取用户信息
     * 2. 校验用户信息
     * 2.1 用户名不允许为空 且 英文开头 仅允许英文、数字、下划线  密码格式验证
     * 2.2 密码 昵称 邮件不允许为空 TODO： 后期邮件需要验证
     * 2.3 验证 用户名/邮箱是否重复
     * 3. 单向加密 用户密码
     * 4. 持久化入库
     *
     * @param dto 用户数据
     */
    @PostMapping("/register")
    public CommonResponse<Boolean> register(@RequestBody @Validated(InsertAction.class) UserDTO dto) {
        return userService.register(dto);
    }

    /**
     * 登录流程
     * 1. 获取登录信息
     * 2. 校验用户信息
     * 2.1 根据用户名查询用户信息
     * 2.2 比对获取的用户密码加密是否和数据库密码是否一致
     * 3. 登录成功，
     * 3.1 用户缓存队列 key为 用户名 ；   value 为 当前mill + space + username 对称加密为 key，加密关键字 密码；
     * 3.2 授权缓存队列 用户缓存队列value 作为key + 当前时间
     * 4. 将key作为 cookieValue 传回前端  Authentication  cookieValue  设置生效生效时间为 3 小时
     * <p>
     * 5. cookie 刷新规则  验证拦截器  有Authentication 并且 在有效
     * 6. 每日凌晨3点 定时清理cookie
     * 7. cookie 存在 未失效 最后三十分钟时 允许 cookie失效时间 重置
     * <p>
     * 8. 针对同一用户异地登录  不允许同时在线  后端提示 异地登录 反馈前端  后登录挤掉之前登录 先logout 在登录
     * <p>
     * 9. 注意判断用户是否被禁止登录 如 EMAIL_NOT_USE 邮箱尚未激活
     *
     * @param dto 登录信息
     */
    @PostMapping("/login")
    public CommonResponse<String> login(HttpServletResponse response, @RequestBody @Validated(LoginAction.class) UserDTO dto) {
        return userService.login(response, dto);
    }

    /**
     * 注销流程
     * 1. 获取用户名并判断是否登录
     * 2. 登陆后 获取cookie 判断注销请求者cookie 与服务器缓存一致
     * 3. 清除用户缓存、授权缓存
     *
     */
    @GetMapping("/logout")
    public CommonResponse<Boolean> logout(@RequestHeader("Authentication") @NotNull(message = "授权头不得为空") String authentication) {
        return userService.logout(authentication);
    }

    /**
     * 获取用户信息
     *
     * @param authentication 授权头
     * @return 三段式返回
     */
    @GetMapping("/info")
    public CommonResponse<UserVO> getUserInfo(@RequestHeader("Authentication") @NotNull(message = "授权头不得为空") String authentication) {
        return userService.getUserInfo(authentication);
    }

    /**
     * 替换头像步骤
     * 1. 缓存图片
     * 2。更新用户图片存储地
     */
    @PostMapping("/avatar")
    public CommonResponse<Boolean> changeAvatar(@RequestHeader("Authentication") @NotNull(message = "授权头不得为空") String authentication, @RequestBody MultipartFile file) {
        return userService.changeAvatar(authentication, file);
    }

    /**
     * 更新用户名
     *
     * @param authentication 授权
     * @param userName       新用户名
     * @return 三段式返回
     */
    @GetMapping("/userName")
    public CommonResponse<Boolean> changeUserName(@RequestHeader("Authentication") @NotNull(message = "授权头不得为空") String authentication, @RequestParam("name") @NotNull(message = "用户名参数不允许为空") String userName) {
        return userService.changeUserName(authentication, userName);
    }

    /**
     * 更新昵称
     *
     * @param authentication 授权
     * @param nickName       新用户名
     * @return 三段式返回
     */
    @GetMapping("/nickName")
    public CommonResponse<Boolean> changeNickName(@RequestHeader("Authentication") @NotNull(message = "授权头不得为空") String authentication, @RequestParam("name") @NotNull(message = "昵称参数不允许为空") String nickName) {
        return userService.changeNickName(authentication, nickName);
    }

    /**
     * 重设密码 post
     * 需要用户定位 及 原密码
     */
    @PostMapping("/password")
    public CommonResponse<Boolean> resetPassword(@RequestHeader("Authentication") @NotNull(message = "授权头不得为空") String authentication, @RequestParam("oldPwd") String oldPwd,
                                                 @RequestParam("newPwd") String newPwd) {
        return userService.resetPassword(authentication, oldPwd, newPwd);
    }

    /**
     * 忘记密码 发送重置密码（随机密码）
     *
     * @param email   邮件
     * @param authentication    验证
     * @param request 请求
     * @return 返回
     */
    @GetMapping("/email/reset")
    public CommonResponse<Boolean> sendEmailRestPwd(@RequestParam("email") @NotNull(message = "邮箱参数不允许为空") String email,
                                                    @RequestHeader("Authentication") String authentication, HttpServletRequest request) {
        return userService.sendEmailRestPwd(email, authentication, request);
    }

    /**
     * 判断是否存在用户 存在及默认为已初始化
     *
     * @return 三段式返回
     */
    @GetMapping("/init")
    public CommonResponse<Boolean> init() {
        return userService.init();
    }


    /**
     * 验证授权是否有效
     *
     * @param authentication 授权码
     * @return 三段式
     */
    @GetMapping("/check/authentication")
    public CommonResponse<Boolean> checkAuthentication(@RequestHeader("Authentication") String authentication) {
        return userService.checkAuthentication(authentication);
    }
}
