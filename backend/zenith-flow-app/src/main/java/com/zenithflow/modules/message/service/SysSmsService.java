package com.zenithflow.modules.message.service;

import com.zenithflow.common.service.CrudService;
import com.zenithflow.modules.message.dto.SysSmsDTO;
import com.zenithflow.modules.message.entity.SysSmsEntity;

/**
 * 短信
 *
 *
 */
public interface SysSmsService extends CrudService<SysSmsEntity, SysSmsDTO> {

    /**
     * 发送短信
     * @param smsCode   短信编码
     * @param mobile   手机号
     * @param params   短信参数
     */
    void send(String smsCode, String mobile, String params);

    SysSmsEntity getBySmsCode(String smsCode);

}

