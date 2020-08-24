package com.xianyu.open.autocode.config;

import com.xianyu.open.autocode.constants.codetype.AutoCodeControllerType;
import com.xianyu.open.autocode.constants.codetype.AutoCodeDaoType;

import java.util.List;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class AutoCodeProperties {

    private List<String> tables;
    private String rootPackage;
    private AutoCodeDaoType autoCodeDaoType;
    private AutoCodeControllerType autoCodeControllerType;

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }

    public AutoCodeDaoType getAutoCodeDaoType() {
        return autoCodeDaoType;
    }

    public void setAutoCodeDaoType(AutoCodeDaoType autoCodeDaoType) {
        this.autoCodeDaoType = autoCodeDaoType;
    }

    public AutoCodeControllerType getAutoCodeControllerType() {
        return autoCodeControllerType;
    }

    public void setAutoCodeControllerType(AutoCodeControllerType autoCodeControllerType) {
        this.autoCodeControllerType = autoCodeControllerType;
    }
}
