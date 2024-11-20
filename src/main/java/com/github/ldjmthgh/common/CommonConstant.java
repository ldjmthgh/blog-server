package com.github.ldjmthgh.common;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public final class CommonConstant {
    private CommonConstant() {
    }

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final JavaType JACKSON_JAVA_TYPE = objectMapper.getTypeFactory()
            .constructParametricType(ArrayList.class, String.class);

    // 用户名正则
    public static final String REGEX_USER_NAME = "^[a-zA-Z][a-zA-Z_0-9]{2,15}$";
    // 密码正则 密码控制只能输入字母、数字、特殊符号(~!@#$%^&*()_+[]{}|\;:'",./<>?)
    // 包括至少1个字母，1个数字，1个特殊字符
    public static final String REGEX_USER_PWD = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![0-9a-zA-Z]+$)(?![0-9\\W]+$)(?![a-zA-Z\\W]+$)[0-9A-Za-z\\W]{8,16}$";
    // 邮箱格式 正则
    public static final String REGEX_USER_EMAIL = "^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$";

    // 认证字段
    public static final String COMMON_STR_AUTHENTICATION = "Authentication";

    // 3h
    public static final Long THREE_HOUR_SECOND = 3 * 60 * 60L;
    // 5min
    public static final Long FIVE_MINUTE_SECOND = 5 * 60L;

    /**
     * str序列化为list
     *
     * @param str 字符串
     * @return list
     * @throws IOException 反序列化异常
     */
    public static List<String> strToStringList(String str) throws IOException {
        if (null == str) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(str, JACKSON_JAVA_TYPE);
    }

    /**
     * str 转对象集合
     *
     * @param str   字符串
     * @param <T>   对象class
     * @param clazz c
     * @return 对象集合
     * @throws IOException io
     */
    public static <T> List<T> strToObjectList(String str, Class clazz) throws IOException {
        if (null == str) {
            return new ArrayList<>();
        }
        JavaType jackSonObjectType = objectMapper.getTypeFactory()
                .constructParametricType(ArrayList.class, clazz);
        return objectMapper.readValue(str, jackSonObjectType);
    }

    /**
     * list序列化为str
     *
     * @param list list
     * @return str
     * @throws IOException 序列化异常
     */
    public static String listToStr(List<String> list) throws IOException {
        return objectMapper.writeValueAsString(list);
    }

    /**
     * 验证用户名格式
     *
     * @param userName 用户名
     * @return 结果
     */
    public static boolean checkUserName(String userName) {
        return Pattern.matches(REGEX_USER_NAME, userName);
    }

    /**
     * 验证密码格式
     *
     * @param pwd 密码
     * @return 结果
     */
    public static boolean checkPwd(String pwd) {
        return Pattern.matches(REGEX_USER_PWD, pwd);
    }

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱
     * @return 结果
     */
    public static boolean checkEmail(String email) {
        return Pattern.matches(REGEX_USER_EMAIL, email);
    }

    /**
     * http 文件下载
     *
     * @param file 文件
     * @return 文件流
     */
    public static ResponseEntity<Resource> download(File file) {
        if (!file.exists()) {
            throw new RuntimeException("请求资源异常，资源链接已经失效");
        }
        String fileType = null;
        try {
            fileType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            log.error("解析文件MIME类型失败", e);
            fileType = MediaType.ALL.toString();
        }
        String contentDisposition = ContentDisposition
                .builder("attachment")
                .filename(file.getName())
                .build()
                .toString();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.valueOf(fileType))
                .body(new FileSystemResource(file.getAbsolutePath()));
    }
}
