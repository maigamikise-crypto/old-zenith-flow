package com.zenithflow.modules.mp.service;

import com.zenithflow.common.service.CrudService;
import com.zenithflow.modules.mp.dto.MpMenuDTO;
import com.zenithflow.modules.mp.entity.MpMenuEntity;

/**
 * 公众号自定义菜单
 *
 *
 */
public interface MpMenuService extends CrudService<MpMenuEntity, MpMenuDTO> {

    MpMenuDTO getByAppId(String appId);

    void deleteByAppId(String appId);
}
