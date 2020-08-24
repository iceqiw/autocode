package com.xianyu.open.autocode;

import com.xianyu.open.autocode.config.AutoCodeProperties;
import com.xianyu.open.autocode.constants.codetype.AutoCodeControllerType;
import com.xianyu.open.autocode.constants.codetype.AutoCodeDaoType;
import com.xianyu.open.autocode.execute.AutoCodeExecute;
import com.xianyu.open.autocode.jdbc.JdbcConfig;
import com.xianyu.open.autocode.util.AutoCodeEnvUtil;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class AutoCode{
    private AutoCode(AutoCodeBuilder builder){
        AutoCodeProperties autoCodeProperties = new AutoCodeProperties();
        autoCodeProperties.setTables(builder.tables);
        autoCodeProperties.setRootPackage(builder.rootPackage);
        autoCodeProperties.setAutoCodeDaoType(builder.autoCodeDaoType);
        autoCodeProperties.setAutoCodeControllerType(builder.autoCodeControllerType);

        JdbcConfig jdbcConfig = new JdbcConfig(builder.dataSource, autoCodeProperties);

        AutoCodeEnvUtil.setAutoCodeProperties(autoCodeProperties);
        AutoCodeEnvUtil.setJdbcConfig(jdbcConfig);

        new AutoCodeExecute().execute();
    }

    public static final class AutoCodeBuilder{
        /**
         * 数据源  必须
         */
        private DataSource dataSource;

        /**
         * 构建表  必须
         */
        private List<String> tables = new ArrayList<>();

        /**
         * 根包    必须
         */
        private String rootPackage;

        /**
         * dao构建方式
         */
        private AutoCodeDaoType autoCodeDaoType = AutoCodeDaoType.MYBATIS;

        /**
         * controller构建方式
         */
        private AutoCodeControllerType autoCodeControllerType = AutoCodeControllerType.WEB_MVC;

        public AutoCodeBuilder setDataSource(DataSource dataSource){
            this.dataSource = dataSource;
            return this;
        }

        public AutoCodeBuilder addTables(String tableName){
            this.tables.add(tableName);
            return this;
        }

        public AutoCodeBuilder setRootPackage(String rootPackage){
            this.rootPackage = rootPackage;
            return this;
        }

        public AutoCodeBuilder setAutoCodeDaoType(AutoCodeDaoType autoCodeDaoType){
            this.autoCodeDaoType = autoCodeDaoType;
            return this;
        }

        public AutoCodeBuilder setAutoCodeController(AutoCodeControllerType autoCodeController){
            this.autoCodeControllerType = autoCodeController;
            return this;
        }

        public AutoCode build(){
            if (dataSource == null || tables.size() == 0 || StringUtils.isEmpty(rootPackage)){
                throw new RuntimeException("参数不够");
            }
            return new AutoCode(this);
        }

    }

}
