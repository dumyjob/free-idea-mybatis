package com.wuzhizhan.mybatis.util;

import com.intellij.openapi.project.Project;
import com.wuzhizhan.mybatis.dom.model.Map;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SqlMapUtils {

    @NotNull
    @NonNls
    public static Collection<Map> findMappers(@NotNull Project project) {
        return DomUtils.findDomElements(project, Map.class);
    }
}
