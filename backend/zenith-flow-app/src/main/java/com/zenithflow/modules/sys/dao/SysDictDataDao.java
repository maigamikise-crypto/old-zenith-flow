package com.zenithflow.modules.sys.dao;

import com.zenithflow.common.dao.BaseDao;
import com.zenithflow.modules.sys.entity.DictData;
import com.zenithflow.modules.sys.entity.SysDictDataEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字典数据
 *
 *
 */
@Mapper
public interface SysDictDataDao extends BaseDao<SysDictDataEntity> {

    /**
     * 字典数据列表
     */
    List<DictData> getDictDataList();
}
