/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */
package com.zenithflow.modules.flow.controller;

import com.zenithflow.common.constant.Constant;
import com.zenithflow.common.exception.ErrorCode;
import com.zenithflow.common.page.PageData;
import com.zenithflow.common.utils.Result;
import com.zenithflow.modules.flow.dto.HistoryDetailDTO;
import com.zenithflow.modules.flow.dto.ProcessInstanceDTO;
import com.zenithflow.modules.flow.dto.TaskDTO;
import com.zenithflow.modules.flow.service.FlowProcessService;
import com.zenithflow.modules.flow.service.FlowService;
import com.zenithflow.modules.flow.user.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 通用流程接口
 *
 *
 */
@RestController
@RequestMapping("/flow/common")
@AllArgsConstructor
@Api(tags = "通用流程接口")
public class FlowController {
    private final FlowProcessService flowProcessService;
    private final FlowService flowService;

    @GetMapping("start/page")
    @ApiOperation("发起流程列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "key", value = "key", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "processName", value = "processName", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:flow:all")
    public Result<PageData<Map<String, Object>>> startPage(@ApiIgnore @RequestParam Map<String, Object> params) {
        params.put("isLatestVersion", true);
        PageData<Map<String, Object>> page = flowProcessService.page(params);
        return new Result<PageData<Map<String, Object>>>().ok(page);
    }

    @GetMapping("form/{processDefinitionId}")
    @ApiOperation("获取流程表单")
    @RequiresPermissions("sys:flow:all")
    public Result<String> form(@PathVariable("processDefinitionId") String processDefinitionId) {
        String formContent = flowService.form(processDefinitionId);

        return new Result<String>().ok(formContent);
    }

    @GetMapping("form/instance/{processInstanceId}")
    @ApiOperation("获取流程实例表单值")
    @RequiresPermissions("sys:flow:all")
    public Result<Map<String, Object>> formInstanceVariables(@PathVariable("processInstanceId") String processInstanceId) {
        Map<String, Object> formVariables = flowService.formInstanceVariables(processInstanceId);

        return new Result<Map<String, Object>>().ok(formVariables);
    }

    @PostMapping("start/instance/{processDefinitionId}")
    @ApiOperation("启动流程实例")
    @RequiresPermissions("sys:flow:all")
    public Result<String> startInstance(@PathVariable("processDefinitionId") String processDefinitionId,
                                        @RequestBody Map<String, Object> variables) {
        flowService.startInstance(processDefinitionId, variables);

        return new Result<>();
    }

    @GetMapping("my/page")
    @ApiOperation("我的申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
    })
    @RequiresPermissions("sys:flow:all")
    public Result<ProcessInstanceDTO> myPage(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ProcessInstanceDTO> page = flowService.myPage(params);
        return new Result().ok(page);
    }

    @GetMapping("done/page")
    @ApiOperation("已办任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "processInstanceId", value = "实例ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "businessKey", value = "业务KEY", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:flow:all")
    public Result<ProcessInstanceDTO> donePage(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<ProcessInstanceDTO> page = flowService.donePage(params);

        return new Result().ok(page);
    }

    @GetMapping("todo/page")
    @ApiOperation("待办任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "taskName", value = "任务名称", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:flow:all")
    public Result<PageData<TaskDTO>> todoPage(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<TaskDTO> page = flowService.todoPage(params);
        return new Result<PageData<TaskDTO>>().ok(page);
    }

    @GetMapping("diagram/image")
    @ApiOperation(value = "获取流程活动图", produces = "application/octet-stream")
    @ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", paramType = "query", dataType = "String")
    @RequiresPermissions("sys:flow:all")
    public void diagramImage(String processInstanceId, @ApiIgnore HttpServletResponse response) throws Exception {
        flowService.diagramImage(processInstanceId, response);
    }

    @GetMapping("historic/list")
    @ApiOperation("获取流转详情列表")
    @ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", paramType = "query", dataType = "String")
    @RequiresPermissions("sys:flow:all")
    public Result<HistoryDetailDTO> historicTaskList(String processInstanceId) {
        List<HistoryDetailDTO> list = flowService.historicTaskList(processInstanceId);
        return new Result().ok(list);
    }


    @PostMapping("delegate")
    @ApiOperation("委托任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "委托人", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:flow:all")
    public Result delegate(String taskId, String userId) {
        if (StringUtils.isBlank(taskId) || StringUtils.isBlank(userId)) {
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }

        flowService.delegate(taskId, LoginUser.getUserId().toString(), userId);

        return new Result();
    }

    @PostMapping("complete")
    @ApiOperation("完成任务(同意)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "comment", value = "审批意见", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:flow:all")
    public Result complete(String taskId, String comment) {
        if (StringUtils.isEmpty(taskId)) {
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }

        flowService.complete(taskId, comment, true);

        return new Result();
    }

    @PostMapping("reject")
    @ApiOperation("驳回")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "comment", value = "审批意见", paramType = "query", dataType = "String")
    })
    @RequiresPermissions("sys:flow:all")
    public Result reject(String taskId, String comment) {
        if (StringUtils.isEmpty(taskId)) {
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }

        flowService.reject(taskId, comment);

        return new Result();
    }
}
