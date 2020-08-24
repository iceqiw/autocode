package com.xianyu.open.autocode.parse.javaparse;

import com.xianyu.open.autocode.constants.AutoCodeModuleType;
import com.xianyu.open.autocode.parse.javaparse.controller.WebMvcControllerCodeParse;
import com.xianyu.open.autocode.util.AutoCodeEnvUtil;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public abstract class ControllerCodeParse extends JavaCodeParse {
    {
        moduleType = AutoCodeModuleType.CONTROLLER;
    }

    public static ControllerCodeParse createControllerCodeParse(){
        ControllerCodeParse controllerCodeParse;
        switch (AutoCodeEnvUtil.getAutoCodeProperties().getAutoCodeControllerType()){
            case WEB_MVC:
                controllerCodeParse = new WebMvcControllerCodeParse();
                break;
            default:
                throw new RuntimeException("hehe");
        }
        return controllerCodeParse;
    }
}
