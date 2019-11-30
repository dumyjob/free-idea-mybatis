package com.wuzhizhan.ibatis.provider;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.wuzhizhan.ibatis.util.SqlMapUtils;
import com.wuzhizhan.mybatis.dom.model.*;
import com.wuzhizhan.mybatis.provider.SimpleLineMarkerProvider;
import com.wuzhizhan.mybatis.util.IbatisUtils;
import com.wuzhizhan.mybatis.util.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Victor von Doom
 * 提供ibatis statement到Ibatis Dao method的跳转
 */
public class IbatisStatementLineMarkerProvider extends SimpleLineMarkerProvider<XmlTag, PsiMethod> {

    private static final ImmutableList<Class<? extends GroupTwo>> TARGET_TYPES = ImmutableList.of(
            Select.class,
            Update.class,
            Insert.class,
            Delete.class
    );

    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
        // 判断是否是需要处理的ibatis中的xml element
        return element instanceof XmlTag
                && SqlMapUtils.isElementWithinMybatisFile(element)
                && isTargetType(element);
    }


    @NotNull
    @Override
    public Optional<PsiMethod> apply(@NotNull XmlTag from) {
        DomElement domElement = DomUtil.getDomElement(from);
        return null == domElement ? Optional.<PsiMethod>absent() : IbatisUtils.findMethod(from.getProject(), (IdDomElement) domElement);
    }

    private boolean isTargetType(PsiElement element) {
        DomElement domElement = DomUtil.getDomElement(element);
        for (Class<?> clazz : TARGET_TYPES) {
            if (clazz.isInstance(domElement))
                return true;
        }
        return false;
    }


    @NotNull
    @Override
    public Navigatable getNavigatable(@NotNull XmlTag from, @NotNull PsiMethod target) {
        return (Navigatable) target.getNavigationElement();
    }

    @NotNull
    @Override
    public String getTooltip(@NotNull XmlTag from, @NotNull PsiMethod target) {
        return "Data access object found - " + target.getContainingClass().getQualifiedName();
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Icons.STATEMENT_LINE_MARKER_ICON;
    }

}
