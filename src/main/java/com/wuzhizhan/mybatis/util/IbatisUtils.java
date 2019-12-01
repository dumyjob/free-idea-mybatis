package com.wuzhizhan.mybatis.util;

import com.google.common.base.Optional;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndex;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.wuzhizhan.ibatis.util.SqlMapUtils;
import com.wuzhizhan.mybatis.dom.model.IdDomElement;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Victor von Doom
 */
public class IbatisUtils {

    public static final String[] SQL_TEMPLATE_METHOD_NAMES = new String[] { "insert" , "update", "delete", "queryForObject", "queryForList"};

    public static final String SQL_MAP_ROOT_TAG = "sqlMap";

    public static String findNamespaceWithId(PsiMethod psiMethod) {
        // 基于语义分析
        PsiCodeBlock body = psiMethod.getBody();
        //+TODO body maybe null in interface
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

    /**
     * xmlFile否是ibatis SqlMap文件
     * @param xmlFile xml file
     * @return true: 是ibatis SqlMap文件 false: 不是ibatis SqlMap文件
     */
    public static boolean isIbatisFile(XmlFile xmlFile){
        XmlTag rootTag = xmlFile.getRootTag();
        return rootTag != null && StringUtils.equals(rootTag.getName(), SQL_MAP_ROOT_TAG);
    }

    public static boolean isIbatisFile(PsiFile file) {
        return DomUtils.isXmlFile(file) && isIbatisFile((XmlFile) file);
    }


    public static Optional<PsiMethod> findMethod(Project project, IdDomElement idDomElement){
        // 通过ibatis statement找到对应的ibatis dao method
        String fullId = SqlMapUtils.getFullId(idDomElement);


        List<PsiMethod> matches = new ArrayList<>();

        ProjectFileIndex.SERVICE.getInstance(project).iterateContent(virtualFile -> {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);

            if(!(psiFile instanceof PsiJavaFile)){
                // 如果不是java文件,go on next
                return true;
            }

            PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
            for(PsiClass psiClass : psiJavaFile.getClasses()){
                if(psiClass.isInterface()){
                    // 如果是interface, continue: 分析不了语义
                    continue;
                }


                for(PsiMethod psiMethod : psiClass.getMethods()){
                    PsiCodeBlock body = psiMethod.getBody();
                    if(body == null){
                        // 可能没有body, class是interface的情况
                        continue;
                    }
                    PsiStatement[] statements = body.getStatements();
                    for(PsiStatement statement : statements){
                        String text = statement.getText();

                        if(StringUtils.contains(text, fullId)){
                            // 找到符合要求的PsiMethod
                            matches.add(psiMethod);
                        }
                    }
                }

            }

            return true;
        });

        return CollectionUtils.isNotEmpty(matches) ? Optional.of(matches.get(0)) : Optional.absent();
    }




}
