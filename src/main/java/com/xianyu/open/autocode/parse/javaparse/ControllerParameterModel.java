package com.xianyu.open.autocode.parse.javaparse;

import java.lang.annotation.Annotation;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class ControllerParameterModel {
    private Class<? extends Annotation> restMethodType;
    private String restUrl;
    private String urlParameter;

    public ControllerParameterModel(Class<? extends Annotation> restMethodType, String restUrl, String urlParameter) {
        this.restMethodType = restMethodType;
        this.restUrl = restUrl;
        this.urlParameter = urlParameter;
    }

    public Class<? extends Annotation> getRestMethodType() {
        return restMethodType;
    }

    public void setRestMethodType(Class<? extends Annotation> restMethodType) {
        this.restMethodType = restMethodType;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    public String getUrlParameter() {
        return urlParameter;
    }

    public void setUrlParameter(String urlParameter) {
        this.urlParameter = urlParameter;
    }
}
