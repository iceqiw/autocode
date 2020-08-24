package com.xianyu.open.autocode.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class StringFormatUtil {

    private final static Pattern UNDERLINE_RULE = Pattern.compile("_(\\w)");

    /**
     * 下划线转属性小驼峰格式
     * @param param
     * @return
     */
    public static String toFiledName(String param){
        if (param == null || "".equals(param)){
            return "";
        }
        StringBuffer sb = toCamelCase(param);
        param = sb.replace(0,1,String.valueOf(Character.toLowerCase(sb.charAt(0)))).toString();
        return param;
    }

    /**
     * 下划线转雷鸣大驼峰格式
     * @param param
     * @return
     */
    public static String toClassName(String param){
        if (param == null || "".equals(param)){
            return "";
        }
        StringBuffer sb = toCamelCase(param);
        param = sb.replace(0,1,String.valueOf(Character.toUpperCase(sb.charAt(0)))).toString();
        return param;

    }

    /**
     * 转小驼峰
     * @param param
     * @return
     */
    private static StringBuffer toCamelCase(String param){
        Matcher matcher = UNDERLINE_RULE.matcher(param);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()){
            matcher.appendReplacement(stringBuffer, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer;
    }

}
