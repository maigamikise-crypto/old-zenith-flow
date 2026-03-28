/**
 * Copyright (c) 2018  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.flow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zenithflow.common.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Map;


/**
 * flowable模型
 *
 *
 */
@Data
@Api(tags = "flowable模型")
public class ModelDTO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "流程key")
    private String key;

    @ApiModelProperty(value = "流程名称")
    private String name;

    @ApiModelProperty(value = "部署ID")
    private String deploymentId;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private Date created;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private Date lastUpdated;

    @ApiModelProperty(value = "流程分类")
    private String category;

    @ApiModelProperty(value = "BPMN XML")
    private String bpmnXml;

    private Map<String, String> metaInfo;

    private ProcessDefinitionDTO processDefinition;

}
