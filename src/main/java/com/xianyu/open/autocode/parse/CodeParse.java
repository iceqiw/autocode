package com.xianyu.open.autocode.parse;

import com.xianyu.open.autocode.jdbc.JdbcConfig;
import com.xianyu.open.autocode.config.AutoCodeProperties;
import com.xianyu.open.autocode.util.AutoCodeEnvUtil;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public abstract class CodeParse {

    public JdbcConfig jdbcConfig = AutoCodeEnvUtil.getJdbcConfig();

    public AutoCodeProperties autoCodeProperties = AutoCodeEnvUtil.getAutoCodeProperties();

    public String filePath;

    public String fileName;

    public String tableName;

    /**
     * 执行自编码入口
     */
    public abstract void parse();

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
