package com.xianyu.open.autocode.factory;

import com.xianyu.open.autocode.parse.CodeParse;
import com.xianyu.open.autocode.parse.javaparse.*;
import com.xianyu.open.autocode.parse.xmlparse.XmlParse;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class CodeParseFactoryBean {


    public CodeParse createMybatisXmlParse(){
        return new XmlParse();
    }

    public CodeParse createModelCodeParse(){
        // todo JPA注解风格待完成
        return new ModelCodeParse();
    }

    public CodeParse createDaoCodeParser(){
        // todo JPA DAO待实现
        return DaoCodeParse.createDaoCodeParse();
    }

    public CodeParse createServiceCodePaser(){
        return new ServiceCodeParse();
    }

    public CodeParse createControllerCodePaser(){
        // todo WebFlux待实现
        return ControllerCodeParse.createControllerCodeParse();
    }
}
