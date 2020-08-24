package com.xianyu.open.autocode.parse.javaparse;

import com.xianyu.open.autocode.constants.AutoCodeMethod;
import com.xianyu.open.autocode.constants.AutoCodeModuleType;
import com.xianyu.open.autocode.parse.CodeParse;
import com.xianyu.open.autocode.util.AutoCodeEnvUtil;
import com.xianyu.open.autocode.util.StringFormatUtil;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.xianyu.open.autocode.util.AutoCodeEnvUtil.getModulePath;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public abstract class JavaCodeParse extends CodeParse {


    public final static Map<String,ControllerParameterModel> CONTROLLER_URL_PARAMETER = new HashMap<>();

    public AutoCodeModuleType moduleType;

    public String rootPath = AutoCodeEnvUtil.getAbsolutePath("java");


    private void writeFile(String filePath, String fileName,String source) {
        Path path = Paths.get(filePath);
        Path file = Paths.get(filePath+"/"+fileName);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            if (Files.exists(file)){
                return;
            }
            else {
                Files.createFile(file);
            }
            Files.write(file,source.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void parse() {
        Map<String, String> javaCodeMap = getJavaCode(tableName, jdbcConfig.getTableAndColumn().get(tableName));
        // 生成文件夹路径与文件名
        filePath = rootPath + AutoCodeEnvUtil.getModulePath(tableName, moduleType);
        fileName = javaCodeMap.get("name");
        String javaCode = javaCodeMap.get("code");
        if (null != javaCode && !"".equals(javaCode)){
            writeFile(filePath,fileName,javaCode);
        }
    }

    /**
     * 获取构建好的java代码
     * @param tableName
     * @param column
     * @return
     */
    public abstract Map<String, String> getJavaCode(String tableName, Map<String,String> column);


    /**
     * 设置接口方法（无方法体）
     * @param type
     * @param tableName
     * @param column
     * @return
     */
    public void getInterfaceMethods(ClassOrInterfaceDeclaration type
            , String tableName, Map<String,String> column){

        String primaryKey = column.get("PK_NAME");
        Class primaryKeyType = jdbcConfig.getColumnClass(column.get(primaryKey));

        // 查询单条
        type.addMethod(AutoCodeMethod.GET_PK.getValue()).setType(tableName)
                .addParameter(primaryKeyType, primaryKey).setBody(null);
        CONTROLLER_URL_PARAMETER.putIfAbsent(AutoCodeMethod.GET_PK.getValue(), new ControllerParameterModel(
                GetMapping.class, "", primaryKey
        ));

        // 查询多条
        type.addMethod(AutoCodeMethod.GET_PARAM.getValue()).setType("List<"+tableName+">")
                .addParameter(tableName,StringFormatUtil.toFiledName(tableName)).setBody(null);
        CONTROLLER_URL_PARAMETER.putIfAbsent(AutoCodeMethod.GET_PARAM.getValue(), new ControllerParameterModel(
                PostMapping.class, "/list", ""
        ));

        // 更新单条
        type.addMethod(AutoCodeMethod.UPDATE_PK.getValue()).setType(int.class)
                .addParameter(tableName,StringFormatUtil.toFiledName(tableName)).setBody(null);
        CONTROLLER_URL_PARAMETER.putIfAbsent(AutoCodeMethod.UPDATE_PK.getValue(), new ControllerParameterModel(
                PutMapping.class, "", ""
        ));

        // 单条增加
        type.addMethod(AutoCodeMethod.INSERT.getValue()).setType(int.class)
                .addParameter(tableName,StringFormatUtil.toFiledName(tableName)).setBody(null);
        CONTROLLER_URL_PARAMETER.putIfAbsent(AutoCodeMethod.INSERT.getValue(), new ControllerParameterModel(
                PostMapping.class, "", ""
        ));

        // 增加多条
//        type.addMethod(AutoCodeMethod.INSERT_BATCH.getValue()).setType(int.class)
//                .addParameter("List<"+tableName+">",StringFormatUtil.toFiledName(tableName)+"s").setBody(null);
//        CONTROLLER_URL_PARAMETER.putIfAbsent(AutoCodeMethod.INSERT_BATCH.getValue(), new ControllerParameterModel(
//                PostMapping.class, "/list", ""
//        ));

        // 删除单条
        type.addMethod(AutoCodeMethod.DELETE_PK.getValue()).setType(int.class)
                .addParameter(primaryKeyType,primaryKey).setBody(null);
        CONTROLLER_URL_PARAMETER.putIfAbsent(AutoCodeMethod.DELETE_PK.getValue(), new ControllerParameterModel(
                DeleteMapping.class, "", primaryKey
        ));

        // 删除多条
        type.addMethod(AutoCodeMethod.DELETE_BATCH.getValue()).setType(int.class)
                .addParameter("List<"+primaryKeyType.getSimpleName()+">", primaryKey+"s").setBody(null);
        CONTROLLER_URL_PARAMETER.putIfAbsent(AutoCodeMethod.DELETE_BATCH.getValue(), new ControllerParameterModel(
                DeleteMapping.class, "/list", ""
        ));

    }

    /**
     * 设置类方法（含方法体）
     * @param type
     * @param tableName
     * @param column
     * @param moduleType
     * @return
     */
    public void getClassMethods(ClassOrInterfaceDeclaration type
            , String tableName, Map<String,String> column, AutoCodeModuleType moduleType){

        getInterfaceMethods(type,tableName,column);

        String fieldValue = StringFormatUtil.toFiledName(tableName+
                StringFormatUtil.toClassName(moduleType.getValue()));

        type.getMethods().forEach(method->{

            method.setModifier(Modifier.PUBLIC, true);

            if ("int".equals(method.getType().asString())){
                BlockStmt blockStmt = new BlockStmt();
                blockStmt.addStatement(new ExpressionStmt().setExpression(fieldValue+"."+
                        method.getName()+"("+method.getParameter(0).getName()+")"));
                method.setBody(blockStmt);
                method.setType(void.class);
            }else {
                BlockStmt blockStmt = new BlockStmt();
                blockStmt.addStatement(new ReturnStmt(fieldValue+"."+
                        method.getName()+"("+method.getParameter(0).getName()+")"));
                method.setBody(blockStmt);
            }
        });
    }
}
