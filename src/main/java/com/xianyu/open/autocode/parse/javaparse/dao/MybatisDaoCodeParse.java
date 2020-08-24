package com.xianyu.open.autocode.parse.javaparse.dao;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.xianyu.open.autocode.constants.AutoCodeModuleType;
import com.xianyu.open.autocode.parse.javaparse.DaoCodeParse;
import com.xianyu.open.autocode.util.AutoCodeEnvUtil;
import com.xianyu.open.autocode.util.StringFormatUtil;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class MybatisDaoCodeParse extends DaoCodeParse {
    @Override
    public Map<String, String> getJavaCode(String tableName, Map<String, String> column) {
        Map<String, String> map = new HashMap<>(16);
        map.put("name",tableName+"Dao.java");

        String mapperClassName = StringFormatUtil.toClassName(tableName+"Dao");

        CompilationUnit cu = new CompilationUnit();

        cu.setPackageDeclaration(AutoCodeEnvUtil.getPackage(tableName , AutoCodeModuleType.DAO));
        cu.addImport(AutoCodeEnvUtil.getPackage(tableName, AutoCodeModuleType.MODEL)+"."+tableName);
        cu.addImport(List.class);

        ClassOrInterfaceDeclaration type = cu.addInterface(mapperClassName);

        getInterfaceMethods(type, tableName,column);

        type.addMarkerAnnotation(Mapper.class);

        map.put("code", cu.toString());
        return map;
    }
}
