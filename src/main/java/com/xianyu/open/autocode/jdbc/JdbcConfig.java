package com.xianyu.open.autocode.jdbc;

import com.xianyu.open.autocode.config.AutoCodeProperties;
import com.xianyu.open.autocode.util.StringFormatUtil;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.*;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class JdbcConfig {

    private DataSource dataSource;

    private AutoCodeProperties autoCodeProperties;

    /**
     * 大驼峰表明与（小驼峰列名->jdbc类型）对应Map
     */
    private  Map<String,Map<String,String>> tableAndColumnMap  = new HashMap<>();

    /**
     * 属性名->列
     */
    private  Map<String,String> property2column = new HashMap<>();

    /**
     * 类名->表
     */
    private  Map<String,String> class2table = new HashMap<>();

    /**
     * 数据库类型->jdbc类型
     */
    private Map<Integer, TypeInformation> mateTypeMap = new HashMap<>();

    /**
     * jdbc类型->java类型
     */
    private Map<String,Class> jdbcMap = new HashMap<>();


    public JdbcConfig(DataSource dataSource, AutoCodeProperties autoCodeProperties) {
        this.dataSource = dataSource;
        this.autoCodeProperties = autoCodeProperties;

        mateTypeMap.put(Types.ARRAY, new TypeInformation("ARRAY",
                Object.class));
        mateTypeMap.put(Types.BIGINT, new TypeInformation("BIGINT",
                Long.class));
        mateTypeMap.put(Types.BINARY, new TypeInformation("BINARY",
                byte[].class));
        mateTypeMap.put(Types.BIT, new TypeInformation("BIT",
                Boolean.class));
        mateTypeMap.put(Types.BLOB, new TypeInformation("BLOB",
                byte[].class));
        mateTypeMap.put(Types.BOOLEAN, new TypeInformation("BOOLEAN",
                Boolean.class));
        mateTypeMap.put(Types.CHAR, new TypeInformation("CHAR",
                String.class));
        mateTypeMap.put(Types.CLOB, new TypeInformation("CLOB",
                String.class));
        mateTypeMap.put(Types.DATALINK, new TypeInformation("DATALINK",
                Object.class));
        mateTypeMap.put(Types.DATE, new TypeInformation("DATE",
                Date.class));
        mateTypeMap.put(Types.DECIMAL, new TypeInformation("DECIMAL",
                BigDecimal.class));
        mateTypeMap.put(Types.DISTINCT, new TypeInformation("DISTINCT",
                Object.class));
        mateTypeMap.put(Types.DOUBLE, new TypeInformation("DOUBLE",
                Double.class));
        mateTypeMap.put(Types.FLOAT, new TypeInformation("FLOAT",
                Double.class));
        mateTypeMap.put(Types.INTEGER, new TypeInformation("INTEGER",
                Integer.class));
        mateTypeMap.put(Types.JAVA_OBJECT, new TypeInformation("JAVA_OBJECT",
                Object.class));
        mateTypeMap.put(Types.LONGNVARCHAR, new TypeInformation("LONGNVARCHAR",
                String.class));
        mateTypeMap.put(Types.LONGVARBINARY, new TypeInformation(
                "LONGVARBINARY",
                byte[].class));
        mateTypeMap.put(Types.LONGVARCHAR, new TypeInformation("LONGVARCHAR",
                String.class));
        mateTypeMap.put(Types.NCHAR, new TypeInformation("NCHAR",
                String.class));
        mateTypeMap.put(Types.NCLOB, new TypeInformation("NCLOB",
                String.class));
        mateTypeMap.put(Types.NVARCHAR, new TypeInformation("NVARCHAR",
                String.class));
        mateTypeMap.put(Types.NULL, new TypeInformation("NULL",
                Object.class));
        mateTypeMap.put(Types.NUMERIC, new TypeInformation("NUMERIC",
                BigDecimal.class));
        mateTypeMap.put(Types.OTHER, new TypeInformation("OTHER",
                Object.class));
        mateTypeMap.put(Types.REAL, new TypeInformation("REAL",
                Float.class));
        mateTypeMap.put(Types.REF, new TypeInformation("REF",
                Object.class));
        mateTypeMap.put(Types.SMALLINT, new TypeInformation("SMALLINT",
                Short.class));
        mateTypeMap.put(Types.STRUCT, new TypeInformation("STRUCT",
                Object.class));
        mateTypeMap.put(Types.TIME, new TypeInformation("TIME",
                Date.class));
        mateTypeMap.put(Types.TIMESTAMP, new TypeInformation("TIMESTAMP",
                Date.class));
        mateTypeMap.put(Types.TINYINT, new TypeInformation("TINYINT",
                Byte.class));
        mateTypeMap.put(Types.VARBINARY, new TypeInformation("VARBINARY",
                byte[].class));
        mateTypeMap.put(Types.VARCHAR, new TypeInformation("VARCHAR",
                String.class));

        mateTypeMap.forEach((key, typeInformation)->{
            jdbcMap.put(typeInformation.getJdbcType(), typeInformation.getJavaType());
        });
    }

    /**
     * 设置需要进行自动编码的表的各个对应关系
     */
    private void setTableAndColumn(){
        try {

            //获取所要自动编码的表集合
            List<String> autoTables = autoCodeProperties.getTables();

            //获取数据库信息
            DatabaseMetaData metaData = getConnection().getMetaData();

            for (String tableName: autoTables){
                //字段信息map
                Map<String,String> columnMap = new LinkedHashMap<>();

                ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
                while (primaryKeys.next()){
                    columnMap.put("PK_NAME", StringFormatUtil.toFiledName(primaryKeys.getString("COLUMN_NAME").toLowerCase()));
                }

                ResultSet columns = metaData.getColumns(null, "%", tableName, "%");
                while (columns.next()){
                    TypeInformation dataType = mateTypeMap.get(columns.getInt("DATA_TYPE"));
                    property2column.put(StringFormatUtil.toFiledName(columns.getString("COLUMN_NAME").toLowerCase()), columns.getString("COLUMN_NAME"));
                    columnMap.put(StringFormatUtil.toFiledName(columns.getString("COLUMN_NAME").toLowerCase()),dataType.getJdbcType());
                }
                class2table.put(StringFormatUtil.toClassName(tableName.toLowerCase()), tableName);
                tableAndColumnMap.put(StringFormatUtil.toClassName(tableName.toLowerCase()), columnMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Map<String, Map<String, String>> getTableAndColumn(){
        if (tableAndColumnMap.size() == 0){
            setTableAndColumn();
        }
        return tableAndColumnMap;
    }

    public Map<String, String> getProperty2column() {
        if (property2column.size() == 0){
            setTableAndColumn();
        }
        return property2column;
    }

    public Map<String, String> getClass2table() {
        if (property2column.size() == 0){
            setTableAndColumn();
        }
        return class2table;
    }

    private Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Map<String, Class> getJdbcMap() {
        return jdbcMap;
    }

    public Class getColumnClass(String columnType){
        return jdbcMap.get(columnType);
    }
}
