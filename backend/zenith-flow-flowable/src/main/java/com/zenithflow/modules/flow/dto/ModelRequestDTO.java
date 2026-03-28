/**
 * Copyright (c) 2018  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.flow.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * flowable请求模型
 *
 *
 */
@Data
@Api(tags = "flowable请求模型")
public class ModelRequestDTO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "流程key")
    private String key;

    @ApiModelProperty(value = "流程名称")
    private String name;

    @ApiModelProperty(value = "BPMN XML")
    private String bpmnXml;

    @ApiModelProperty(value = "表单ID")
    private String formId;

    @ApiModelProperty(value = "表单名称")
    private String formName;
}
