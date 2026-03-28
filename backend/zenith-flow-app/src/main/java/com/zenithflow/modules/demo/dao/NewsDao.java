package com.zenithflow.modules.demo.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.demo.entity.NewsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 新闻
 *
 *
 */
@Mapper
public interface NewsDao extends BaseDao<NewsEntity> {

    List<NewsEntity> getList(Map<String, Object> params);

}
