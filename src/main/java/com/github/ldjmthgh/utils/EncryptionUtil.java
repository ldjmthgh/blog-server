package com.github.ldjmthgh.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;

public final class EncryptionUtil {

    /**
     * 获取对称加密
     *
     * @param appKey 对称加密 对
     * @return re
     */
    public static AES getDateAES(String appKey) {
        return new AES("ECB", "PKCS7Padding", appKey.getBytes());
    }

    /**
     * 获取对称加密结果
     *
     * @param appKey 对称加密钥
     * @param source 加密源
     * @return 加密结果
     */
    public static String getAESResult(String appKey, String source) {
        return new AES("ECB", "PKCS7Padding", appKey.getBytes()).decryptStr(source);
    }

    /**
     * 获取对称加密
     *
     * @param appKey 对称加密 对
     * @return re
     */
    public static DES getDateDES(String appKey) {
        return new DES("ECB", "PKCS7Padding", appKey.getBytes());
    }

    /**
     * 获取对称加密结果
     *
     * @param appKey 对称加密钥
     * @param source 加密源
     * @return 加密结果
     */
    public static String getDESResult(String appKey, String source) {
        return new DES("ECB", "PKCS7Padding", appKey.getBytes()).decryptStr(source);
    }


    /**
     * 单向加密 摘要加密
     *
     * @param in in
     * @return re
     */
    public static String getSha1Re(String in) {
        Digester digester = new Digester(DigestAlgorithm.SHA1);
        return digester.digestHex(in);
    }


    /**
     * 先对称解密，再单向加密 摘要加密
     * @param appKey 对称加密钥
     * @param in in
     * @return re
     */
    public static String getAESResultThenSha1Re(String appKey, String in) {
        Digester digester = new Digester(DigestAlgorithm.SHA1);
        return digester.digestHex(EncryptionUtil.getAESResult(appKey, in));
    }


}
