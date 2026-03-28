package com.zenithflow.modules.sys.service;


import com.zenithflow.common.page.PageData;
import com.zenithflow.common.service.BaseService;
import com.zenithflow.modules.sys.dto.SysRoleDTO;
import com.zenithflow.modules.sys.entity.SysRoleEntity;

import java.util.List;
import java.util.Map;


/**
 * 角色
 *
 *
 */
public interface SysRoleService extends BaseService<SysRoleEntity> {

	PageData<SysRoleDTO> page(Map<String, Object> params);

	List<SysRoleDTO> list(Map<String, Object> params);

	SysRoleDTO get(Long id);

	void save(SysRoleDTO dto);

	void update(SysRoleDTO dto);

	void delete(Long[] ids);

}
