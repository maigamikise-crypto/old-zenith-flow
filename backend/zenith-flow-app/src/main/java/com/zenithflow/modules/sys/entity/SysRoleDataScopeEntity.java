package com.zenithflow.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zenithflow.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色数据权限
 *
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_role_data_scope")
public class SysRoleDataScopeEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 角色ID
	 */
	private Long roleId;
	/**
	 * 部门ID
	 */
	private Long deptId;

}
