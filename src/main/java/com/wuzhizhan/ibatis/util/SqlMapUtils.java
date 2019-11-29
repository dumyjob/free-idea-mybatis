package com.wuzhizhan.ibatis.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.wuzhizhan.ibatis.dom.model.SqlMap;
import com.wuzhizhan.mybatis.dom.model.IdDomElement;
import com.wuzhizhan.mybatis.util.DomUtils;
import com.wuzhizhan.mybatis.util.IbatisUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

/**
 * author: Victor von Doom
 */
public class SqlMapUtils {

    @NotNull
    @NonNls
    public static Collection<SqlMap> findSqlMaps(@NotNull Project project) {
        // 查找项目中的所有sqlMap
        return DomUtils.findDomElements(project, SqlMap.class);
    }

    @NotNull
    @NonNls
    public static <T extends IdDomElement> String getFullId(@NotNull T domElement) {
        // 获取namespace + "." + id
        return getNamespace(domElement) + "." + getId(domElement);
    }


    @Nullable
    @NonNls
    public static <T extends IdDomElement> String getId(@NotNull T domElement) {
        return domElement.getId().getRawText();
    }

    @NotNull
    @NonNls
    public static String getNamespace(@NotNull DomElement element) {
        return getNamespace(getMapper(element));
    }

    @NotNull
    @NonNls
    public static String getNamespace(@NotNull SqlMap sqlMap) {
        String ns = sqlMap.getNamespace().getStringValue();
        return null == ns ? "" : ns;
    }

    @NotNull
    @NonNls
    public static SqlMap getMapper(@NotNull DomElement element) {
        Optional<SqlMap> optional = Optional.ofNullable(DomUtil.getParentOfType(element, SqlMap.class, true));
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new IllegalArgumentException("Unknown element");
        }
    }


    public static boolean isElementWithinMybatisFile(@NotNull PsiElement element) {
        PsiFile psiFile = element.getContainingFile();
        return element instanceof XmlElement && IbatisUtils.isIbatisFile(psiFile);
    }



}
