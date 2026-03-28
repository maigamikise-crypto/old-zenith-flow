/**
 * Copyright (c) 2020  All rights reserved.
 * <p>
 * 
 * <p>
 * 版权所有，侵权必究！
 */

package com.zenithflow.modules.devtools.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zenithflow.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * 模板管理
 *
 *
 */
@Data
@TableName("gen_template")
public class TemplateEntity {
	/**
	 * id
	 */
	@TableId
	private Long id;
    /**
     * 名称
     */
	private String name;
    /**
     * 内容
     */
	private String content;
    /**
     * 文件名
     */
	private String fileName;
	/**
	 * 生成路径
	 */
	private String path;
	/**
	 * 状态  0：启用   1：禁用
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date createDate;

}
