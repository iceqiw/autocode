package com.xianyu.open.autocode.execute;

import com.xianyu.open.autocode.factory.CodeParseFactoryBean;
import com.xianyu.open.autocode.parse.CodeParse;
import com.xianyu.open.autocode.util.AutoCodeEnvUtil;
import com.xianyu.open.autocode.util.StringFormatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class AutoCodeExecute {
    private List<CodeParse> codeParses = new ArrayList<>();

    public AutoCodeExecute() {
        CodeParseFactoryBean codeParseFactoryBean = new CodeParseFactoryBean();

        switch (AutoCodeEnvUtil.getAutoCodeProperties().getAutoCodeDaoType()){
            case MYBATIS:
                codeParses.add(codeParseFactoryBean.createMybatisXmlParse());
                break;
            default:
                throw new RuntimeException("hehe");
        }

        codeParses.add(codeParseFactoryBean.createModelCodeParse());
        codeParses.add(codeParseFactoryBean.createDaoCodeParser());
        codeParses.add(codeParseFactoryBean.createServiceCodePaser());
        codeParses.add(codeParseFactoryBean.createControllerCodePaser());

    }

    public void execute() {
        List<String> tables = AutoCodeEnvUtil.getAutoCodeProperties().getTables();
        tables.forEach(tableName -> codeParses.forEach(codeParse -> {
            codeParse.setTableName(StringFormatUtil.toClassName(tableName));
            codeParse.parse();
        }));
    }
}
