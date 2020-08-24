package com.xianyu.open.autocode.constants;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public enum AutoCodeModuleType {

    /**
     * 控制层
     */
    CONTROLLER("controller"),

    /**
     * 业务层
     */
    SERVICE("service"),

    /**
     * dao层
     */
    DAO("dao"),

    /**
     * 持久层
     */
    MODEL("model");

    private String value;

    AutoCodeModuleType(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
