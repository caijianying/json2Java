package com.javaear.json4bean.util;

/**
 * @author aooer
 */
public abstract class StringUtils {

    /* 前置空间 */
    public static Boolean isCreateMultiBean = false;

    /* 前置空间 */
    public static final String PREFIX_SPACE = "    ";

    /* 系统换行符 */
    public static final String LINE = System.getProperty("line.separator");

    public static final char UNDERLINE = '_';

    /**
     * 第一个字母转大写
     *
     * @param sourceStr 原str
     * @return 转换后的str
     */
    public static String upperCaseFirstChar(String sourceStr) {
        sourceStr = underlineToCamel(sourceStr);
        String s = String.valueOf(Character.toUpperCase(sourceStr.charAt(0))) + sourceStr.substring(1);
        return s;
    }

    /**
     * 空字符串判断
     *
     * @param sourceStr sourceStr
     * @return 是否为空
     */
    public static boolean isEmpty(String sourceStr) {
        return sourceStr == null || "".equals(sourceStr);
    }

    public static String underlineToCamel(String param) {
        String temp = param.toLowerCase();
        int len = temp.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = temp.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(temp.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
