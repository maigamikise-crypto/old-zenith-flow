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
import com.zenithflow.modules.devtools.entity.DataSourceEntity;
import com.zenithflow.modules.devtools.service.DataSourceService;
import com.zenithflow.modules.devtools.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 数据源管理
 *
 *
 */
@RestController
@RequestMapping("devtools/datasource")
public class DataSourceController {
    @Autowired
    private DataSourceService datasourceService;

    @GetMapping("page")
    public Result<PageData<DataSourceEntity>> page(@RequestParam Map<String, Object> params){
        PageData<DataSourceEntity> page = datasourceService.page(params);

        return new Result<PageData<DataSourceEntity>>().ok(page);
    }

    @GetMapping("list")
    public Result<List<DataSourceEntity>> list(){
        List<DataSourceEntity> list = datasourceService.list();

        return new Result<List<DataSourceEntity>>().ok(list);
    }

    @GetMapping("{id}")
    public Result<DataSourceEntity> get(@PathVariable("id") Long id){
        DataSourceEntity data = datasourceService.selectById(id);

        return new Result<DataSourceEntity>().ok(data);
    }

    @GetMapping("test/{id}")
    public Result<String> test(@PathVariable("id") Long id){
        try {
            DataSourceEntity entity = datasourceService.selectById(id);

            DbUtils.getConnection(new DataSourceInfo(entity));
            return new Result<String>().ok("连接成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<String>().error("连接失败，请检查配置信息");
        }
    }

    @PostMapping
    public Result save(@RequestBody DataSourceEntity entity){
        datasourceService.insert(entity);

        return new Result();
    }

    @PutMapping
    public Result update(@RequestBody DataSourceEntity entity){
        datasourceService.updateById(entity);

        return new Result();
    }

    @DeleteMapping
    public Result delete(@RequestBody Long[] ids){
        datasourceService.deleteBatchIds(Arrays.asList(ids));

        return new Result();
    }
}
