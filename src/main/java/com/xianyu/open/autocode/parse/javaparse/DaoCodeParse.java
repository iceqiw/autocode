package com.xianyu.open.autocode.parse.javaparse;

import com.xianyu.open.autocode.constants.AutoCodeModuleType;
import com.xianyu.open.autocode.parse.javaparse.dao.MybatisDaoCodeParse;
import com.xianyu.open.autocode.util.AutoCodeEnvUtil;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public abstract class DaoCodeParse extends JavaCodeParse {
    {
        moduleType = AutoCodeModuleType.DAO;
    }

    public static DaoCodeParse createDaoCodeParse(){
        DaoCodeParse daoCodeParse;
        switch (AutoCodeEnvUtil.getAutoCodeProperties().getAutoCodeDaoType()){
            case MYBATIS:
                daoCodeParse = new MybatisDaoCodeParse();
                break;
            default:
                throw new RuntimeException("hehe");
        }
        return daoCodeParse;
    }
}
