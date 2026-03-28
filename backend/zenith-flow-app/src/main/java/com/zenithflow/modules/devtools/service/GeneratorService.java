/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.service;

import com.zenithflow.modules.devtools.config.DataSourceInfo;
import com.zenithflow.modules.devtools.entity.MenuEntity;
import com.zenithflow.modules.devtools.entity.TableFieldEntity;
import com.zenithflow.modules.devtools.entity.TableInfoEntity;

import java.util.List;

/**
 * 代码生成
 *
 *
 */
public interface GeneratorService {

    DataSourceInfo getDataSourceInfo(Long datasourceId);

    void datasourceTable(TableInfoEntity tableInfo);

    void updateTableField(Long tableId, List<TableFieldEntity> tableFieldList);

    void generatorCode(TableInfoEntity tableInfo);

    void generatorMenu(MenuEntity menu);
}
