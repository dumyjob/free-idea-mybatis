package com.wuzhizhan.ibatis.provider;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomUtil;
import com.wuzhizhan.ibatis.util.SqlMapUtils;
import com.wuzhizhan.mybatis.dom.model.*;
import com.wuzhizhan.mybatis.util.IbatisUtils;
import com.wuzhizhan.mybatis.util.Icons;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Victor von Doom
 * 提供ibatis statement到Ibatis Dao method的跳转
 */
public class IbatisStatementLineMarkerProvider extends RelatedItemLineMarkerProvider{

    private static final ImmutableList<Class<? extends GroupTwo>> TARGET_TYPES = ImmutableList.of(
            Select.class,
            Update.class,
            Insert.class,
            Delete.class
    );


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        if(!isTarget(element)){
            return ;
        }

        DomElement domElement = DomUtil.getDomElement(element);
        Optional<PsiMethod> processResult = IbatisUtils.findMethod(element.getProject(), (IdDomElement) domElement);

        if(processResult.isPresent()){

            NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(Icons.STATEMENT_LINE_MARKER_ICON)
                            .setAlignment(GutterIconRenderer.Alignment.CENTER)
                            .setTargets(processResult.get())
                            .setTooltipTitle("Navigation to target in ibatis dao");

            result.add(builder.createLineMarkerInfo(element));
        }


    }

    private boolean isTarget(PsiElement element) {
        // 判断是否是目标对象 select | update | insert | delete
        if (!(element instanceof XmlTag)) {
            return false;
        }

        XmlTag xmlTag = (XmlTag) element;

        DomElement domElement = DomManager.getDomManager(xmlTag.getProject()).getDomElement(xmlTag);
        return isTargetType(domElement) && SqlMapUtils.isElementWithinMybatisFile(element);
    }

    private boolean isTargetType(DomElement domElement) {
        for (Class<?> clazz : TARGET_TYPES) {
            if (clazz.isInstance(domElement))
                return true;
        }
        return false;
    }
}
