/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.controller;

import com.google.common.collect.Maps;
import com.zenithflow.common.exception.RenException;
import com.zenithflow.common.page.PageData;
import com.zenithflow.common.utils.Result;
import com.zenithflow.modules.devtools.entity.TemplateEntity;
import com.zenithflow.modules.devtools.service.TemplateService;
import com.zenithflow.modules.devtools.utils.GenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 模板管理
 *
 *
 */
@RestController
@RequestMapping("devtools/template")
public class TemplateController {
    @Autowired
    private TemplateService templateService;

    @GetMapping("page")
    public Result<PageData<TemplateEntity>> page(@RequestParam Map<String, Object> params){
        PageData<TemplateEntity> page = templateService.page(params);

        return new Result<PageData<TemplateEntity>>().ok(page);
    }

    @GetMapping("{id}")
    public Result<TemplateEntity> get(@PathVariable("id") Long id){
        TemplateEntity data = templateService.selectById(id);

        return new Result<TemplateEntity>().ok(data);
    }

    @PostMapping
    public Result save(@RequestBody TemplateEntity entity){
        try {
            //检查模板语法是否正确
            GenUtils.getTemplateContent(entity.getContent(), Maps.newHashMap());
        } catch (Exception e) {
            throw new RenException("模板语法错误，请查看控制台报错信息！", e);
        }

        templateService.insert(entity);

        return new Result();
    }

    @PutMapping
    public Result update(@RequestBody TemplateEntity entity){
        try {
            //检查模板语法是否正确
            GenUtils.getTemplateContent(entity.getContent(), Maps.newHashMap());
        } catch (Exception e) {
            throw new RenException("模板语法错误，请查看控制台报错信息！", e);
        }
        templateService.updateById(entity);

        return new Result();
    }

    @DeleteMapping
    public Result delete(@RequestBody Long[] ids){
        templateService.deleteBatchIds(Arrays.asList(ids));

        return new Result();
    }

    /**
     * 启用
     */
    @PutMapping("enabled")
    public Result enabled(@RequestBody Long[] ids){
        templateService.updateStatusBatch(ids, 0);

        return new Result();
    }

    /**
     * 禁用
     */
    @PutMapping("disabled")
    public Result disabled(@RequestBody Long[] ids){
        templateService.updateStatusBatch(ids, 1);

        return new Result();
    }
}
