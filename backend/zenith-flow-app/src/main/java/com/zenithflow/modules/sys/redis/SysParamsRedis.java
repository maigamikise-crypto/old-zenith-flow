package com.zenithflow.modules.sys.redis;

import com.zenithflow.common.redis.RedisKeys;
import com.zenithflow.common.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 参数管理
 *
 *
 */
@Component
public class SysParamsRedis {
    @Autowired
    private RedisUtils redisUtils;

    public void delete(Object[] paramCodes) {
        String key = RedisKeys.getSysParamsKey();
        redisUtils.hDel(key, paramCodes);
    }

    public void set(String paramCode, String paramValue){
        if(paramValue == null){
            return ;
        }
        String key = RedisKeys.getSysParamsKey();
        redisUtils.hSet(key, paramCode, paramValue);
    }

    public String get(String paramCode){
        String key = RedisKeys.getSysParamsKey();
        return (String)redisUtils.hGet(key, paramCode);
    }

}
