/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */
package com.zenithflow.modules.ureport.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zenithflow.common.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 报表管理
 *
 *
 */
@Data
@ApiModel(value = "报表管理")
public class UReportDataDTO implements Serializable {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "报表文件名")
    private String fileName;
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private Date updateDate;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private Date createDate;
}
