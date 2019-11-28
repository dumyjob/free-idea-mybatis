package com.wuzhizhan.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.Processor;
import com.wuzhizhan.mybatis.dom.model.IdDomElement;
import com.wuzhizhan.mybatis.dom.model.Map;
import com.wuzhizhan.mybatis.util.IbatisUtils;
import com.wuzhizhan.mybatis.util.MapperUtils;
import com.wuzhizhan.mybatis.util.SqlMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class IbatisService {

    private Project project;

    public IbatisService(Project project) {
        this.project = project;
    }

    public static IbatisService getInstance(@NotNull Project project){
        return ServiceManager.getService(project, IbatisService.class);
    }

    public void process(PsiElement element, Processor<IdDomElement> processor) {
        if(element instanceof PsiClass){
            // ibatis中的SqlDao和SqlMap并不直接关联.
            return;
        }

        // ibatis是通过SqlDao method中指定的namespace.id来关联到SqlMap中的insert/select/update/delete
        if(element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            process(method, processor);
        }
    }

    private void process(PsiMethod psiMethod, Processor<IdDomElement> processor){
        // SqlMapDaoTemplate提供的访问sqlMap的方法:
        // insert  | update | delete | query*
        String id = IbatisUtils.findNamespaceWithId(psiMethod);
        if(id == null){
            // not found statement param in method
            return;
        }

        // MapperUtils.findMappers还是可以使用,ibatis和mybatis定义的结构还是差不多
        for (Map sqlMap : SqlMapUtils.findMappers(psiMethod.getProject())) {
            for (IdDomElement domElement : sqlMap.getDaoElements()) {
                if (StringUtils.equals(MapperUtils.getIdSignature(domElement), id)) {
                    processor.process(domElement);
                }
            }
        }
    }

}
