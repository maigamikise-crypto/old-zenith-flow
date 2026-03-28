package com.zenithflow.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zenithflow.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色用户关系
 *
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_role_user")
public class SysRoleUserEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 角色ID
	 */
	private Long roleId;
	/**
	 * 用户ID
	 */
	private Long userId;

}
