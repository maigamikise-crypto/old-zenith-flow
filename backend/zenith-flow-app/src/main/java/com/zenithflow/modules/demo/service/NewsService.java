package com.zenithflow.modules.demo.service;

import com.zenithflow.common.page.PageData;
import com.zenithflow.common.service.BaseService;
import com.zenithflow.modules.demo.dto.NewsDTO;
import com.zenithflow.modules.demo.entity.NewsEntity;

import java.util.Map;

/**
 * 新闻
 *
 *
 */
public interface NewsService extends BaseService<NewsEntity> {

    PageData<NewsDTO> page(Map<String, Object> params);

    NewsDTO get(Long id);

    void save(NewsDTO dto);

    void update(NewsDTO dto);
}

