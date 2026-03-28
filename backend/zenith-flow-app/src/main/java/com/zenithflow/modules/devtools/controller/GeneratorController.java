/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.controller;

import com.zenithflow.common.page.PageData;
import com.zenithflow.common.utils.Result;
import com.zenithflow.modules.devtools.config.DataSourceInfo;
import com.zenithflow.modules.devtools.entity.MenuEntity;
import com.zenithflow.modules.devtools.entity.TableFieldEntity;
import com.zenithflow.modules.devtools.entity.TableInfoEntity;
import com.zenithflow.modules.devtools.service.GeneratorService;
import com.zenithflow.modules.devtools.service.TableFieldService;
import com.zenithflow.modules.devtools.service.TableInfoService;
import com.zenithflow.modules.devtools.utils.DbUtils;
import com.zenithflow.modules.sys.entity.DictType;
import com.zenithflow.modules.sys.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 代码生成
 *
 *
 */
@RestController
@RequestMapping("devtools")
public class GeneratorController {
    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private TableInfoService tableInfoService;
    @Autowired
    private TableFieldService tableFieldService;
    @Autowired
    private SysDictTypeService sysDictTypeService;

    @GetMapping("table/page")
    public Result<PageData<TableInfoEntity>> pageTable(@RequestParam Map<String, Object> params){
        PageData<TableInfoEntity> page = tableInfoService.page(params);

        return new Result<PageData<TableInfoEntity>>().ok(page);
    }

    @GetMapping("table/{id}")
    public Result<TableInfoEntity> getTable(@PathVariable("id") Long id){
        TableInfoEntity table = tableInfoService.selectById(id);

        List<TableFieldEntity> fieldList = tableFieldService.getByTableName(table.getTableName());
        table.setFields(fieldList);

        return new Result<TableInfoEntity>().ok(table);
    }

    @PutMapping("table")
    public Result updateTable(@RequestBody TableInfoEntity tableInfo){
        tableInfoService.updateById(tableInfo);

        return new Result();
    }

    @DeleteMapping("table")
    public Result deleteTable(@RequestBody Long[] ids){
        tableInfoService.deleteBatchIds(ids);

        return new Result();
    }

    /**
     * 获取数据源中所有表
     */
    @GetMapping("datasource/table/list/{id}")
    public Result<List<TableInfoEntity>> getDataSourceTableList(@PathVariable("id") Long id){
        try {
            //初始化配置信息
            DataSourceInfo info = generatorService.getDataSourceInfo(id);
            List<TableInfoEntity> tableInfoList = DbUtils.getTablesInfoList(info);

            return new Result<List<TableInfoEntity>>().ok(tableInfoList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<List<TableInfoEntity>>().error("数据源配置错误，请检查数据源配置！");
        }
    }

    /**
     * 导入数据源中的表
     */
    @PostMapping("datasource/table")
    public Result datasourceTable(@RequestBody TableInfoEntity tableInfo) {
        generatorService.datasourceTable(tableInfo);

        return new Result();
    }

    /**
     * 更新列数据
     */
    @PutMapping("table/field/{tableId}")
    public Result updateTableField(@PathVariable("tableId") Long tableId, @RequestBody List<TableFieldEntity> tableFieldList) {
        generatorService.updateTableField(tableId, tableFieldList);

        return new Result();
    }

    @GetMapping("dict")
    public Result<List<DictType>> dict(){
        List<DictType> list = sysDictTypeService.getDictTypeList();

        return new Result<List<DictType>>().ok(list);
    }

    /**
     * 生成代码
     */
    @PostMapping("generator")
    public Result generator(@RequestBody TableInfoEntity tableInfo) {
        //保存表信息
        tableInfoService.updateById(tableInfo);

        //生成代码
        generatorService.generatorCode(tableInfo);

        return new Result<>();
    }

    /**
     * 创建菜单
     */
    @PostMapping("menu")
    public Result menu(@RequestBody MenuEntity menu) {
        //创建菜单
        generatorService.generatorMenu(menu);

        return new Result<>();
    }
}
