package com.zenithflow.modules.oss.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zenithflow.common.utils.DateUtils;
import lombok.Data;

import java.util.Date;

/**
 * 文件上传
 *
 *
 */
@Data
@TableName("sys_oss")
public class SysOssEntity {
	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * URL地址
	 */
	private String url;
	/**
	 * 创建者
	 */
	@TableField(fill = FieldFill.INSERT)
	private Long  creator;
	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date createDate;
}
