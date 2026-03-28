package com.zenithflow.modules.sys.service;

import com.zenithflow.common.page.PageData;
import com.zenithflow.common.service.BaseService;
import com.zenithflow.modules.sys.dto.SysDictDataDTO;
import com.zenithflow.modules.sys.entity.SysDictDataEntity;

import java.util.Map;

/**
 * 数据字典
 *
 *
 */
public interface SysDictDataService extends BaseService<SysDictDataEntity> {

    PageData<SysDictDataDTO> page(Map<String, Object> params);

    SysDictDataDTO get(Long id);

    void save(SysDictDataDTO dto);

    void update(SysDictDataDTO dto);

    void delete(Long[] ids);

}
