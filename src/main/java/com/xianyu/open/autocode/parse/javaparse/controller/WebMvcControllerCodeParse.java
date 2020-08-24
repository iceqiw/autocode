package com.xianyu.open.autocode.parse.javaparse.controller;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.xianyu.open.autocode.constants.AutoCodeModuleType;
import com.xianyu.open.autocode.parse.javaparse.ControllerCodeParse;
import com.xianyu.open.autocode.parse.javaparse.ControllerParameterModel;
import com.xianyu.open.autocode.util.AutoCodeEnvUtil;
import com.xianyu.open.autocode.util.StringFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class WebMvcControllerCodeParse extends ControllerCodeParse {
    @Override
    public Map<String, String> getJavaCode(String tableName, Map<String, String> column) {
        Map<String, String> map = new HashMap<>(16);
        map.put("name",tableName+"Controller.java");

        String controllerClassName = StringFormatUtil.toClassName(tableName+"Controller");
        String serviceFieldType = tableName+"Service";
        String serviceFieldValue = StringFormatUtil.toFiledName(tableName+"Service");

        CompilationUnit cu = new CompilationUnit();

        cu.setPackageDeclaration(AutoCodeEnvUtil.getPackage(tableName , AutoCodeModuleType.CONTROLLER));
        cu.addImport(AutoCodeEnvUtil.getPackage(tableName, AutoCodeModuleType.MODEL)+"."+tableName);
        cu.addImport(AutoCodeEnvUtil.getPackage(tableName, AutoCodeModuleType.SERVICE)+"."+serviceFieldType);
        cu.addImport(List.class);

        ClassOrInterfaceDeclaration type = cu.addClass(controllerClassName);
        type.addMarkerAnnotation(RestController.class);
        type.addSingleMemberAnnotation(RequestMapping.class, "\"/"+StringFormatUtil.toFiledName(tableName)+"\"");

        type.addField(serviceFieldType, serviceFieldValue, Modifier.PRIVATE).addMarkerAnnotation(Autowired.class);

        getClassMethods(type, tableName,column, AutoCodeModuleType.SERVICE);

        type.getMethods().forEach(method->{
            String methodName = method.getNameAsString();
            ControllerParameterModel controllerParameterModel = CONTROLLER_URL_PARAMETER.get(methodName);
            if (column.get("PK_NAME").equals(method.getParameter(0).getNameAsString())){
                method.addSingleMemberAnnotation(controllerParameterModel.getRestMethodType(),
                        "\""+controllerParameterModel.getRestUrl()+"/{"+ controllerParameterModel.getUrlParameter() +"}\"")
                        .getParameter(0).addMarkerAnnotation(PathVariable.class);
            }else {
                MethodDeclaration methodDeclaration;
                if ("".equals(controllerParameterModel.getRestUrl())){
                    methodDeclaration = method.addMarkerAnnotation(controllerParameterModel.getRestMethodType());
                }else {
                    methodDeclaration = method.addSingleMemberAnnotation(controllerParameterModel.getRestMethodType(),
                            "\""+controllerParameterModel.getRestUrl()+"\"");
                }
                methodDeclaration.getParameter(0).addMarkerAnnotation(RequestBody.class);

            }
        });

        map.put("code", cu.toString());
        return map;
    }
}
