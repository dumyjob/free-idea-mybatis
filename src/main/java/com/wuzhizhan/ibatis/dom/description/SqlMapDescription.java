package com.wuzhizhan.ibatis.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import com.wuzhizhan.ibatis.dom.model.SqlMap;
import com.wuzhizhan.mybatis.util.IbatisUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author : Victor von Doom
 * 需要在plugin.xml中使用<dom.fileDescription> 进行注册(ps: intellij version 2019.1 before)
 */
public class SqlMapDescription extends DomFileDescription<SqlMap> {
    public SqlMapDescription() {
        super(SqlMap.class, IbatisUtils.SQL_MAP_ROOT_TAG);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
       return IbatisUtils.isIbatisFile(file);
    }
}
