package com.xianyu.open.autocode.parse.javaparse;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.xianyu.open.autocode.constants.AutoCodeModuleType;
import com.xianyu.open.autocode.util.AutoCodeEnvUtil;
import com.xianyu.open.autocode.util.StringFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class ServiceCodeParse extends JavaCodeParse {

    public ServiceCodeParse() {
        moduleType = AutoCodeModuleType.SERVICE;
    }

    @Override
    public Map<String, String> getJavaCode( String tableName, Map<String, String> column) {
        Map<String, String> map = new HashMap<>(16);
        map.put("name",tableName+"Service.java");

        String serviceClassName = StringFormatUtil.toClassName(tableName+"Service");
        String mapperFieldType = tableName+"Dao";
        String mapperFieldValue = StringFormatUtil.toFiledName(tableName+"Dao");

        CompilationUnit cu = new CompilationUnit();

        cu.setPackageDeclaration(AutoCodeEnvUtil.getPackage(tableName ,AutoCodeModuleType.SERVICE));
        cu.addImport(AutoCodeEnvUtil.getPackage(tableName, AutoCodeModuleType.MODEL)+"."+tableName);
        cu.addImport(AutoCodeEnvUtil.getPackage(tableName, AutoCodeModuleType.DAO)+"."+mapperFieldType);
        cu.addImport(List.class);

        ClassOrInterfaceDeclaration type = cu.addClass(serviceClassName);
        type.addMarkerAnnotation(Service.class);

        type.addField(mapperFieldType, mapperFieldValue, Modifier.PRIVATE).addMarkerAnnotation(Autowired.class);

        getClassMethods(type,tableName,column, AutoCodeModuleType.DAO);

        map.put("code", cu.toString());
        return map;
    }
}
