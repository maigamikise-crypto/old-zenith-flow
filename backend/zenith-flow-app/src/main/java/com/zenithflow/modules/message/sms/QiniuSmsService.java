package com.zenithflow.modules.message.sms;

import com.qiniu.http.Response;
import com.qiniu.sms.SmsManager;
import com.qiniu.util.Auth;
import com.zenithflow.common.constant.Constant;
import com.zenithflow.common.exception.ErrorCode;
import com.zenithflow.common.exception.RenException;
import com.zenithflow.common.utils.SpringContextUtils;
import com.zenithflow.modules.message.service.SysSmsLogService;

import java.util.LinkedHashMap;

/**
 * 七牛短信服务
 *
 *
 */
public class QiniuSmsService extends AbstractSmsService {
    private SmsManager smsManager;

    public QiniuSmsService(SmsConfig config){
        this.config = config;

        //初始化
        init();
    }


    private void init(){
        Auth auth = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey());
        smsManager = new SmsManager(auth);
    }

    @Override
    public void sendSms(String smsCode, String mobile, LinkedHashMap<String, String> params) {
        this.sendSms(smsCode, mobile, params, null, config.getQiniuTemplateId());
    }

    @Override
    public void sendSms(String smsCode, String mobile, LinkedHashMap<String, String> params, String signName, String template) {
        Response response;
        try {
            response = smsManager.sendSingleMessage(template, mobile, params);
        } catch (Exception e) {
            throw new RenException(ErrorCode.SEND_SMS_ERROR, e, "");
        }

        int status = Constant.SUCCESS;
        if(!response.isOK()){
            status = Constant.FAIL;
        }

        //保存短信记录
        SysSmsLogService sysSmsLogService = SpringContextUtils.getBean(SysSmsLogService.class);
        sysSmsLogService.save(smsCode, Constant.SmsService.QCLOUD.getValue(), mobile, params, status);

        if(status == Constant.FAIL){
            throw new RenException(ErrorCode.SEND_SMS_ERROR, response.error);
        }
    }
}
