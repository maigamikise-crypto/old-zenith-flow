package com.zenithflow.modules.flow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zenithflow.common.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 工作流表单
 *
 *
 */
@Data
@ApiModel(value = "工作流表单")
public class BpmFormDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "表单内容")
    private String content;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
    private Date createDate;

}
