/**
 * Copyright (c) 2021  All rights reserved.
 *
 * 
 *
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.sys.controller;

import com.zenithflow.common.annotation.LogOperation;
import com.zenithflow.common.constant.Constant;
import com.zenithflow.common.page.PageData;
import com.zenithflow.common.utils.Result;
import com.zenithflow.common.validator.AssertUtils;
import com.zenithflow.common.validator.ValidatorUtils;
import com.zenithflow.common.validator.group.AddGroup;
import com.zenithflow.common.validator.group.DefaultGroup;
import com.zenithflow.common.validator.group.UpdateGroup;
import com.zenithflow.modules.sys.dto.SysPostDTO;
import com.zenithflow.modules.sys.service.SysPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
* 岗位管理
*
*
*/
@RestController
@RequestMapping("sys/post")
@Api(tags="岗位管理")
public class SysPostController {
    @Autowired
    private SysPostService sysPostService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:post:page")
    public Result<PageData<SysPostDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<SysPostDTO> page = sysPostService.page(params);

        return new Result<PageData<SysPostDTO>>().ok(page);
    }

    @GetMapping("list")
    @ApiOperation("列表")
    public Result<List<SysPostDTO>> list(){
        Map<String, Object> params = new HashMap<>();
        //正常岗位列表
        params.put("status", "1");
        List<SysPostDTO> data = sysPostService.list(params);

        return new Result<List<SysPostDTO>>().ok(data);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("sys:post:info")
    public Result<SysPostDTO> get(@PathVariable("id") Long id){
        SysPostDTO data = sysPostService.get(id);

        return new Result<SysPostDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:post:save")
    public Result save(@RequestBody SysPostDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        sysPostService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:post:update")
    public Result update(@RequestBody SysPostDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        sysPostService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:post:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        sysPostService.delete(ids);

        return new Result();
    }
}
