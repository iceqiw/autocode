package com.xianyu.open.autocode.util;

import com.xianyu.open.autocode.config.AutoCodeProperties;
import com.xianyu.open.autocode.constants.AutoCodeModuleType;
import com.xianyu.open.autocode.jdbc.JdbcConfig;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class AutoCodeEnvUtil {
    private static AutoCodeProperties autoCodeProperties;
    private static JdbcConfig jdbcConfig;


    public static void setAutoCodeProperties(AutoCodeProperties autoCodeProperties) {
        AutoCodeEnvUtil.autoCodeProperties = autoCodeProperties;
    }

    public static void setJdbcConfig(JdbcConfig jdbcConfig) {
        AutoCodeEnvUtil.jdbcConfig = jdbcConfig;
    }

    /**
     * 获取总配置类
     * @return
     */
    public static AutoCodeProperties getAutoCodeProperties() {
        return autoCodeProperties;
    }

    /**
     * 获取数据库表、列、类、属性配置类
     * @return
     */
    public static JdbcConfig getJdbcConfig() {
        return jdbcConfig;
    }

    /**
     * 根据tableName和module获取具体路径
     * @param tableName
     * @param module
     * @return
     */
    public static String getModulePath(String tableName, AutoCodeModuleType module){
        return "/"+autoCodeProperties.getRootPackage().replaceAll("\\.","/")
                +"/"+tableName.toLowerCase()+"/"+module.getValue();
    }

    /**
     * 根据tableName和module获取包名
     * @param tableName
     * @param module
     * @return
     */
    public static String getPackage(String tableName, AutoCodeModuleType module){
        return autoCodeProperties.getRootPackage() + "." + tableName.toLowerCase()
                + "." + module.getValue();
    }

    /**
     * 根据tableName和module获取完整类名
     * @param tableName
     * @param module
     * @return
     */
    public static String getRealClassName(String tableName, AutoCodeModuleType module){
        return autoCodeProperties.getRootPackage() + "." + tableName.toLowerCase()
                + "." + module.getValue()
                +"."+tableName+ StringFormatUtil.toClassName(module.getValue());
    }


    public static String getAbsolutePath(String path){
        return System.getProperty("user.dir") + "/src/main/" + path;
    }
}
