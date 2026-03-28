/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */
package com.zenithflow.modules.flow.controller;

import com.zenithflow.common.utils.Result;
import com.zenithflow.modules.flow.service.FlowProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

/**
 * 流程管理
 *
 *
 */
@RestController
@RequestMapping("/flow/process")
@AllArgsConstructor
@Api(tags = "流程管理")
public class FlowProcessController {
    private final FlowProcessService flowProcessService;

    @PutMapping("active/{id}")
    @RequiresPermissions("sys:process:all")
    public Result active(@PathVariable("id") String id) {
        flowProcessService.active(id);

        return new Result();
    }

    @PutMapping("suspend/{id}")
    @ApiOperation("挂起流程")
    @RequiresPermissions("sys:process:all")
    public Result suspend(@PathVariable("id") String id) {
        flowProcessService.suspend(id);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除流程")
    @RequiresPermissions("sys:process:all")
    public Result delete(@RequestBody String[] deploymentIds) {
        for (String deploymentId : deploymentIds) {
            flowProcessService.deleteDeployment(deploymentId);
        }
        return new Result();
    }

}
