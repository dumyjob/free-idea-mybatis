package com.wuzhizhan.mybatis.util;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import org.apache.commons.lang3.StringUtils;

public class IbatisUtils {

    public static final String[] SQL_TEMPLATE_METHOD_NAMES = new String[] { "insert" , "update", "delete", "queryForObject", "queryForList"};

    public static String findNamespaceWithId(PsiMethod psiMethod) {
        // 基于语义分析
        // +TODO 查找ibatis Dao Method调用的sqlMap namespace.id
        PsiCodeBlock body = psiMethod.getBody();
        // body maybe null in interface
        PsiStatement[] statements = body.getStatements();

        for(PsiStatement statement :  statements){
            // 打算通过正则表达式匹配来获取方法参数
            String text = statement.getText();
            // 匹配 insert | update | delete | query* 语句中出现的第一个参数就是sqlMap中的statementId: namespace.id
           for(String methodName : SQL_TEMPLATE_METHOD_NAMES){
               int index = StringUtils.indexOf(text, methodName);
               if(index != -1){
                   // 找到对应的sqlDaoTemplate method调用
                   return StringUtils.substring(text, index+ methodName.length() +2, StringUtils.indexOf(text, ",", index + 1) -1);
               }
           }
        }

        // not found statement param
        return null;
    }
}
