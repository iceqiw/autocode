package com.xianyu.open.autocode.parse.javaparse;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.xianyu.open.autocode.constants.AutoCodeModuleType;
import com.xianyu.open.autocode.util.AutoCodeEnvUtil;
import com.xianyu.open.autocode.util.StringFormatUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class ModelCodeParse extends JavaCodeParse {

    public ModelCodeParse() {
        moduleType = AutoCodeModuleType.MODEL;
    }

    @Override
    public Map<String, String> getJavaCode( String tableName, Map<String, String> column) {
        Map<String, String> map = new HashMap<>(16);
        map.put("name",tableName+".java");
        CompilationUnit cu = new CompilationUnit();

        cu.setPackageDeclaration(AutoCodeEnvUtil.getPackage(tableName ,AutoCodeModuleType.MODEL));

        ClassOrInterfaceDeclaration type = cu.addClass(tableName);

        //两个循环为了属性在前方法在后
        column.forEach((key,value)->{
            Class fieldClass = jdbcConfig.getColumnClass(value);
            if (fieldClass!=null){
                type.addPrivateField(fieldClass, key);
            }
        });

        column.forEach((key,value)->{
            Class fieldClass = jdbcConfig.getColumnClass(value);
            if (fieldClass!=null){
                String methodName = StringFormatUtil.toClassName(key);

                MethodDeclaration method = type.addMethod("get"+ methodName, Modifier.PUBLIC).setType(fieldClass);
                BlockStmt block = new BlockStmt();
                block.addStatement(new ReturnStmt(key));
                method.setBody(block);

                method = type.addMethod("set"+methodName,Modifier.PUBLIC);
                method.addParameter(fieldClass, key);
                block = new BlockStmt();
                block.addStatement(new ExpressionStmt().setExpression("this."+key+"="+key));
                method.setBody(block);
            }
        });
        map.put("code",cu.toString());
        return map;
    }
}
