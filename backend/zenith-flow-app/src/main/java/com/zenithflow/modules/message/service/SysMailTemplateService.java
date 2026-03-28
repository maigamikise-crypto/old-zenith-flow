package com.zenithflow.modules.message.service;

import com.zenithflow.common.service.CrudService;
import com.zenithflow.modules.message.dto.SysMailTemplateDTO;
import com.zenithflow.modules.message.entity.SysMailTemplateEntity;

/**
 * 邮件模板
 *
 *
 */
public interface SysMailTemplateService extends CrudService<SysMailTemplateEntity, SysMailTemplateDTO> {

    /**
     * 发送邮件
     * @param id           邮件模板ID
     * @param mailTo       收件人
     * @param mailCc       抄送
     * @param params       模板参数
     */
    boolean sendMail(Long id, String mailTo, String mailCc, String params) throws Exception;
}
