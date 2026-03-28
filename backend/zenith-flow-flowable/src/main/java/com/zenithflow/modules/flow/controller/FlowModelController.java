/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */
package com.zenithflow.modules.flow.controller;

import cn.hutool.core.util.StrUtil;
import com.zenithflow.common.constant.Constant;
import com.zenithflow.common.page.PageData;
import com.zenithflow.common.utils.Result;
import com.zenithflow.common.xss.XssHttpServletRequestWrapper;
import com.zenithflow.modules.flow.dto.ModelDTO;
import com.zenithflow.modules.flow.dto.ModelRequestDTO;
import com.zenithflow.modules.flow.service.FlowModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 模型管理
 *
 *
 */
@RestController
@RequestMapping("/flow/model")
@AllArgsConstructor
@Api(tags = "模型管理")
public class FlowModelController {
    private final FlowModelService flowModelService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "key", value = "key", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "name", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:model:all")
    public Result<PageData<ModelDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ModelDTO> page = flowModelService.page(params);

        return new Result<PageData<ModelDTO>>().ok(page);
    }

    @PostMapping
    @ApiOperation("新建模型")
    @RequiresPermissions("sys:model:all")
    public Result save(HttpServletRequest request, ModelRequestDTO modelDTO) {
        XssHttpServletRequestWrapper requestWrapper = (XssHttpServletRequestWrapper) request;
        String bpmnXml = requestWrapper.getOrgRequest().getParameter("bpmnXml");
        modelDTO.setBpmnXml(bpmnXml);

        if (StrUtil.isNotBlank(modelDTO.getId())) {
            flowModelService.updateModel(modelDTO);
        } else {
            flowModelService.saveModel(modelDTO);
        }

        return new Result();
    }

    @GetMapping("{id}")
    @ApiOperation("获取模型")
    @RequiresPermissions("sys:model:all")
    public Result<ModelDTO> getModel(@PathVariable("id") String id) {
        ModelDTO model = flowModelService.getModel(id);

        return new Result<ModelDTO>().ok(model);
    }

    @PostMapping("deploy/{id}")
    @ApiOperation("部署")
    @RequiresPermissions("sys:model:all")
    public Result deploy(@PathVariable("id") String id) {
        flowModelService.deploymentByModelId(id);
        return new Result();
    }

    @DeleteMapping("{id}")
    @ApiOperation("删除模型")
    @RequiresPermissions("sys:model:all")
    public Result delete(@PathVariable("id") String id) {
        flowModelService.deleteModel(id);

        return new Result();
    }
}
