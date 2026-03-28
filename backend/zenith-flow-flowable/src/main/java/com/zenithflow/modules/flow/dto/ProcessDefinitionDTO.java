package com.zenithflow.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProcessDefinitionDTO {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "是否挂起")
    private boolean isSuspended;
}
