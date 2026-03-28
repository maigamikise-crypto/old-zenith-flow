package com.zenithflow.modules.log.service;


import com.zenithflow.common.page.PageData;
import com.zenithflow.common.service.BaseService;
import com.zenithflow.modules.log.dto.SysLogErrorDTO;
import com.zenithflow.modules.log.entity.SysLogErrorEntity;

import java.util.List;
import java.util.Map;

/**
 * 异常日志
 *
 *
 */
public interface SysLogErrorService extends BaseService<SysLogErrorEntity> {

    PageData<SysLogErrorDTO> page(Map<String, Object> params);

    List<SysLogErrorDTO> list(Map<String, Object> params);

    void save(SysLogErrorEntity entity);

}
