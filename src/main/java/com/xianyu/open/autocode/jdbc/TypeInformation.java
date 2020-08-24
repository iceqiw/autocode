package com.xianyu.open.autocode.jdbc;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class TypeInformation {
    private String jdbcType;
    private Class javaType;

    public TypeInformation(String jdbcType, Class javaType) {
        this.jdbcType = jdbcType;
        this.javaType = javaType;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public Class getJavaType() {
        return javaType;
    }

    public void setJavaType(Class javaType) {
        this.javaType = javaType;
    }
}
