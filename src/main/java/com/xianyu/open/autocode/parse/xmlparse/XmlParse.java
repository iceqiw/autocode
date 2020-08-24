package com.xianyu.open.autocode.parse.xmlparse;

import com.xianyu.open.autocode.constants.AutoCodeMethod;
import com.xianyu.open.autocode.constants.AutoCodeModuleType;
import com.xianyu.open.autocode.parse.CodeParse;
import com.xianyu.open.autocode.util.AutoCodeEnvUtil;
import com.xianyu.open.autocode.util.StringFormatUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class XmlParse extends CodeParse {
    private OutputFormat format;

    public XmlParse() {
        format = OutputFormat.createPrettyPrint();
        format.setTrimText(false);
    }

    @Override
    public void parse() {
        Document document = parse2Document(tableName, jdbcConfig.getTableAndColumn().get(tableName));
        writeFile(AutoCodeEnvUtil.getAbsolutePath("resources/mapper"), StringFormatUtil.toFiledName(tableName)+"-sql.xml", document);
    }

    private void writeFile(String filePath, String fileName, Document document) {
        Path path = Paths.get(filePath);
        Path file = Paths.get(filePath+"/"+fileName);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            if (Files.exists(file)){
                return;
            }else {
                Files.createFile(file);
            }
            XMLWriter xmlWriter = new AutoCodeXMLWriter(new FileOutputStream(file.toFile()),format);
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Document parse2Document(String tableName, Map<String, String> column) {
        Document document = DocumentHelper.createDocument();
        document.addDocType("mapper","-//mybatis.org//DTD Mapper 3.0//EN",
                "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
        Element mapper = document.addElement("mapper");
        mapper.addAttribute("namespace", AutoCodeEnvUtil.getRealClassName(tableName,AutoCodeModuleType.DAO));

        //添加映射
        addResultMap(tableName, column, mapper);

        // 查询字段片段
        String columnList = addSelectList(tableName, column, mapper);

        addCondition(column, mapper);

        // 主键查询
        addSelectByPrimaryKey(tableName, column, mapper);

        // 条件查询
        addSelectByParam(tableName, column, mapper);

        // 更新
        addUpdateByPrimaryKey(tableName, column, mapper);

        // 添加
        String insertList = addInsert(tableName, column, mapper, columnList);

        // 批量添加
//        addInsertBatch(tableName, column, mapper, columnList, insertList);

        // 主键删除
        addDeleteByPrimaryKey(tableName, column, mapper);

        // 批量删除
        addDeleteByPrimaryKeys(tableName, column, mapper);

        return document;
    }

    private void addInsertBatch(String tableName, Map<String, String> column,
                                Element mapper, String columnList, String insertList) {

        String s = insertList.replaceAll("#\\{", "#{item.");

        mapper.addComment("批量插入");
        mapper.addElement("insert")
                .addAttribute("id", AutoCodeMethod.INSERT_BATCH.getValue())
                .addAttribute("parameterType", "java.util.List")
                .addText("insert into "+ jdbcConfig.getClass2table().get(tableName)+" ("+columnList+")values")
                .addElement("foreach")
                .addAttribute("collection","list")
                .addAttribute("item", "item")
                .addAttribute("separator",",")
                .addText("("+s+")");
    }

    private String addInsert(String tableName, Map<String, String> column, Element mapper, String columnList) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        for (Map.Entry<String,String> entry : column.entrySet()){
            if ("PK_NAME".equals(entry.getKey())){
                continue;
            }
            if (i++%4 == 0){
                stringBuilder.append("\n");
            }
            stringBuilder.append("#{")
                    .append(entry.getKey())
                    .append(",jdbcType=")
                    .append(entry.getValue())
                    .append("},");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);

        String sql = "insert into "+ jdbcConfig.getClass2table().get(tableName)+" ("+columnList+")\n"
                +"values ("+stringBuilder.toString()+")";

        mapper.addComment("单条插入");
        mapper.addElement("insert")
                .addAttribute("id",AutoCodeMethod.INSERT.getValue())
                .addAttribute("parameterType", AutoCodeEnvUtil.getPackage(tableName, AutoCodeModuleType.MODEL) + "." + tableName)
                .addText(sql);
        return stringBuilder.toString();
    }

    private void addDeleteByPrimaryKeys(String tableName, Map<String, String> column, Element mapper) {
        String pk_name = column.get("PK_NAME");
        mapper.addComment("根据主键批量删除");
        mapper.addElement("delete")
                .addAttribute("id",AutoCodeMethod.DELETE_BATCH.getValue())
                .addAttribute("parameterType","java.util.List")
                .addText("delete from "+ jdbcConfig.getClass2table().get(tableName)+" where "+pk_name+" in")
                .addElement("foreach")
                .addAttribute("collection","list")
                .addAttribute("item", "item")
                .addAttribute("open","(")
                .addAttribute("separator",",")
                .addAttribute("close",")")
                .addText("#{item,jdbcType="+column.get(pk_name)+"}");
    }

    private void addDeleteByPrimaryKey(String tableName, Map<String, String> column, Element mapper) {
        String pk_name = column.get("PK_NAME");
        String  pk_javaType = jdbcConfig.getColumnClass(column.get(pk_name)).getName();

        String stringBuilder = "delete from " +
                jdbcConfig.getClass2table().get(tableName) +
                "\n" +
                "where " +
                pk_name +
                " = " +
                "#{" +
                pk_name +
                ",jdbcType=" +
                column.get(pk_name) +
                "}";

        mapper.addComment("根据主键删除单条");
        mapper.addElement("delete")
                .addAttribute("id", AutoCodeMethod.DELETE_PK.getValue())
                .addAttribute("parameterType", pk_javaType)
                .addText(stringBuilder);
    }

    private void addUpdateByPrimaryKey(String tableName, Map<String, String> column, Element mapper) {
        String pk_name = column.get("PK_NAME");
        StringBuilder stringBuilder = new StringBuilder();


        mapper.addComment("根据主键更新");
        Element updateByPrimaryKey = mapper.addElement("update")
                .addAttribute("id", AutoCodeMethod.UPDATE_PK.getValue())
                .addAttribute("parameterType", AutoCodeEnvUtil.getPackage(tableName, AutoCodeModuleType.MODEL) + "." + tableName);

        updateByPrimaryKey.addText("update "+ jdbcConfig.getClass2table().get(tableName));

        Element set = updateByPrimaryKey.addElement("set");

        column.forEach((key,value)->{
            if ("PK_NAME".equals(key) || pk_name.equals(key)){
                return;
            }

            String test = key+" != "+"null";
            if ("String".equals(jdbcConfig.getColumnClass(value).getSimpleName())){
                test = test+" and "+key+" != \'\'";
            }
            set.addElement("if")
                    .addAttribute("test", test)
                    .addText(jdbcConfig.getProperty2column().get(key)+" = #{"+key+",jdbcType="+value+"},");

        });

        updateByPrimaryKey.addText("where "+pk_name+" = "+"#{"+pk_name+",jdbcType="+column.get(pk_name)+"}");
    }

    private void addSelectByParam(String tableName, Map<String, String> column, Element mapper) {
        mapper.addComment("根据参数查询");
        Element selectByParam = mapper.addElement("select")
                .addAttribute("id",AutoCodeMethod.GET_PARAM.getValue())
                .addAttribute("parameterType", AutoCodeEnvUtil.getPackage(tableName, AutoCodeModuleType.MODEL) + "." + tableName)
                .addAttribute("resultMap", "BaseResultMap");

        selectByParam.addText("select");
        selectByParam.addElement("include")
                .addAttribute("refid","Select_List");
        selectByParam.addText("from "+ jdbcConfig.getClass2table().get(tableName));
        selectByParam.addElement("where")
                .addElement("include")
                .addAttribute("refid","Filter_Condition");

    }

    private void addCondition(Map<String, String> column, Element mapper) {
        Element sql=mapper.addElement("sql").addAttribute("id","Filter_Condition");
        column.forEach((key,value)->{
            if ("PK_NAME".equals(key)){
                return;
            }
            String test = key+" != "+"null";
            if ("String".equals(jdbcConfig.getColumnClass(value).getSimpleName())){
                test = test+" and "+key+" != \'\'";
            }
            sql.addElement("if")
                    .addAttribute("test", test)
                    .addText("AND "+ jdbcConfig.getProperty2column().get(key)+" = #{"+key+",jdbcType="+value+"}");
        });
    }

    private void addSelectByPrimaryKey(String tableName, Map<String, String> column, Element mapper) {
        String pk_name = column.get("PK_NAME");
        String  pk_javaType = jdbcConfig.getColumnClass(column.get(pk_name)).getName();

        mapper.addComment("根据主键查询");
        Element selectByPrimaryKey = mapper.addElement("select")
                .addAttribute("id", AutoCodeMethod.GET_PK.getValue())
                .addAttribute("parameterType", pk_javaType)
                .addAttribute("resultMap", "BaseResultMap");

        selectByPrimaryKey.addText("select");
        selectByPrimaryKey.addElement("include")
                .addAttribute("refid","Select_List");
        selectByPrimaryKey.addText("from "+ jdbcConfig.getClass2table().get(tableName))
                .addText("where "+pk_name+" = "+"#{"+pk_name+",jdbcType="+column.get(pk_name)+"}");
    }

    private String addSelectList(String tableName, Map<String, String> column, Element mapper) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        for (Map.Entry<String,String> entry : column.entrySet()){
            if ("PK_NAME".equals(entry.getKey())){
                continue;
            }
            if (i++%6 == 0){
                stringBuilder.append("\n");
            }
            stringBuilder.append(jdbcConfig.getProperty2column().get(entry.getKey())).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);

        mapper.addElement("sql")
                .addAttribute("id","Select_List")
                .addText(stringBuilder.toString());
        return stringBuilder.toString();
    }

    private void addResultMap(String tableName, Map<String, String> column, Element mapper) {
        String pk_name = column.get("PK_NAME");

        Element resultMap = mapper.addElement("resultMap")
                .addAttribute("id", "BaseResultMap")
                .addAttribute("type", AutoCodeEnvUtil.getPackage(tableName, AutoCodeModuleType.MODEL) + "."+tableName);

        column.forEach((key,value)->{
            Element result = null;
            if ("PK_NAME".equals(key)){
                return;
            }
            if (key.equals(pk_name)){
                result = resultMap.addElement("id");
            }else {
                result= resultMap.addElement("result");
            }

            result.addAttribute("column", jdbcConfig.getProperty2column().get(key))
                    .addAttribute("property", key)
                    .addAttribute("jdbcType", value);
        });
    }
}
