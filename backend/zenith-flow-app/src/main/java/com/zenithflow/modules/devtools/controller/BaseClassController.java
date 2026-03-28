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
import com.zenithflow.modules.devtools.entity.BaseClassEntity;
import com.zenithflow.modules.devtools.service.BaseClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 基类管理
 *
 *
 */
@RestController
@RequestMapping("devtools/baseclass")
public class BaseClassController {
    @Autowired
    private BaseClassService baseClassService;

    @GetMapping("page")
    public Result<PageData<BaseClassEntity>> page(@RequestParam Map<String, Object> params){
        PageData<BaseClassEntity> page = baseClassService.page(params);

        return new Result<PageData<BaseClassEntity>>().ok(page);
    }

    @GetMapping("list")
    public Result<List<BaseClassEntity>> list(){
        List<BaseClassEntity> list = baseClassService.list();

        return new Result<List<BaseClassEntity>>().ok(list);
    }

    @GetMapping("{id}")
    public Result<BaseClassEntity> get(@PathVariable("id") Long id){
        BaseClassEntity data = baseClassService.selectById(id);

        return new Result<BaseClassEntity>().ok(data);
    }

    @PostMapping
    public Result save(@RequestBody BaseClassEntity entity){
        baseClassService.insert(entity);

        return new Result();
    }

    @PutMapping
    public Result update(@RequestBody BaseClassEntity entity){
        baseClassService.updateById(entity);

        return new Result();
    }

    @DeleteMapping
    public Result delete(@RequestBody Long[] ids){
        baseClassService.deleteBatchIds(Arrays.asList(ids));

        return new Result();
    }
}
