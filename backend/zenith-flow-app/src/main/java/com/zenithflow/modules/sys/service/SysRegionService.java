package com.zenithflow.modules.sys.service;

import com.zenithflow.common.service.BaseService;
import com.zenithflow.modules.sys.dto.SysRegionDTO;
import com.zenithflow.modules.sys.dto.region.RegionProvince;
import com.zenithflow.modules.sys.entity.SysRegionEntity;

import java.util.List;
import java.util.Map;

/**
 * 行政区域
 *
 *
 */
public interface SysRegionService extends BaseService<SysRegionEntity> {

	List<SysRegionDTO> list(Map<String, Object> params);

	List<Map<String, Object>> getTreeList();

	SysRegionDTO get(Long id);

	void save(SysRegionDTO dto);

	void update(SysRegionDTO dto);

	void delete(Long id);

	int getCountByPid(Long pid);

	List<RegionProvince> getRegion(boolean threeLevel);
}
