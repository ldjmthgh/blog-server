package com.github.ldjmthgh.model.dto;



import com.github.ldjmthgh.common.CommonConstant;
import com.github.ldjmthgh.common.InsertAction;
import com.github.ldjmthgh.common.LoginAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserDTO {
    /**
     * 用户名 唯一
     */
    @NotNull(message = "用户名参数不得为空", groups = {LoginAction.class})
    private String userName;
    /**
     * 密码  不对称加密后入库
     */
    @NotNull(message = "用户密码参数不得为空", groups = {InsertAction.class, LoginAction.class})
    private String userPassword;
    /**
     * 头像地址
     */
    private String avatarHttpUrl;
    /**
     * 用户昵称  长度1-16
     */
    @NotNull(message = "用户昵称参数不得为空", groups = {InsertAction.class})
    private String nickName;
    /**
     * 用户email 可用于召回密码
     */
    @NotNull(message = "用户邮箱参数不得为空", groups = {InsertAction.class})
    private String email;


    public boolean checkUserName() {
        return Pattern.matches(CommonConstant.REGEX_USER_NAME, userName);
    }

    public boolean checkPwd() {
        return Pattern.matches(CommonConstant.REGEX_USER_PWD, userPassword);
    }

    public boolean checkNickName() {
        return null != nickName && nickName.length() < 28 && nickName.length() > 1;
    }

    public boolean checkEmail() {
        return Pattern.matches(CommonConstant.REGEX_USER_EMAIL, email);
    }

    public String check() {
        if (!checkUserName()) {
            return "用户名格式异常，必须字母开头，长度在3到16字符之间，且仅允许包含大小写字母、数字、下划线；";
        }
        if (!checkPwd()) {
            return "密码格式异常，仅允许包含大小写字母、数字、特殊符号，长度位8-16，且至少包含1个字母，1个数字和1个特殊字符；";
        }
        if (!checkEmail()) {
            return "邮箱格式异常";
        }
        if (!checkNickName()) {
            return "昵称长度位1-16位";
        }
        return null;
    }
}
