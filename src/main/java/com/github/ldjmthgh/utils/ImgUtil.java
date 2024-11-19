package com.github.ldjmthgh.utils;

/**
 * @Author: ldj
 * @Date: 2021/2/22 21:49
 */
public final class ImgUtil {
    private static final String IMAGE_NAME_BACKSLASH = "image/";
    private static final String[] IMAGE_TYPES = {"jpg", "JPG", "gif", "GIF", "png", "PNG", "bmp", "BMP", "jpeg", "JPEG"};

    /**
     * 判断该类型是否为 图片类型
     *
     * @param type 图片类型
     * @return 校验结果
     */
    public static boolean filterImg(String type) {
        if (null == type || "".equals(type)) {
            return false;
        }
        for (String imageType : IMAGE_TYPES) {
            if (imageType.equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断该类型是否为 图片类型
     *
     * @param type 图片类型
     * @return 校验结果
     */
    public static String filterImgBackslash(String type) {
        if (null == type || "".equals(type)) {
            return null;
        }
        for (String imageType : IMAGE_TYPES) {
            if (type.equals(IMAGE_NAME_BACKSLASH + imageType)) {
                return imageType;
            }
        }
        return null;
    }

    /**
     * 文件名  后缀获取
     *
     * @param path 路径
     * @return 文件后缀
     */
    public static String getImageName(String path) {
        if (!path.contains(".")) {
            return null;
        }
        return path.trim().split("\\.")[1].trim();
    }

}
