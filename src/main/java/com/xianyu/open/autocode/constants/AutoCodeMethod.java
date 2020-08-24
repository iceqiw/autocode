package com.xianyu.open.autocode.constants;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public enum AutoCodeMethod {

    /**
     * 增加单条
     */
    INSERT("add"),

    /**
     * 增加多条
     */
    INSERT_BATCH("addList"),

    /**
     * 删除单条,根据主键
     */
    DELETE_PK("delete"),

    /**
     * 批量删除，根据主键列表
     */
    DELETE_BATCH("deleteList"),

    /**
     * 更新，根据主键
     */
    UPDATE_PK("update"),

    /**
     * 通过主键查询
     */
    GET_PK("get"),

    /**
     * 条件查询
     */
    GET_PARAM("query");

    private String value;

    AutoCodeMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
