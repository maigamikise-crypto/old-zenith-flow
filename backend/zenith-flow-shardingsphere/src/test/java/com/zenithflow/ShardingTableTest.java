package com.zenithflow;

import com.zenithflow.dao.UserLogDao;
import com.zenithflow.entity.UserLogEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 测试 ShardingSphere 分表
 *
 *
 */
@SpringBootTest
public class ShardingTableTest {
    @Resource
    private UserLogDao userLogDao;

    @Test
    public void shardingTest() {
        UserLogEntity log = new UserLogEntity();
        //log.setId(1L);
        log.setName("test");
        log.setContent("测试");
        log.setCreateTime(LocalDateTime.now());

        userLogDao.insert(log);
    }
}
