package com.fox.api.dao.quartz.mapper;

import com.fox.api.annotation.mapper.QuartzMapperConfig;
import com.fox.api.dao.quartz.entity.JobRunLogEntity;

/**
 * @author lusongsong
 * @date 2020/10/12 15:51
 */
@QuartzMapperConfig
public interface JobRunLogMapper {
    /**
     * 插入
     * @param jobRunLogEntity
     * @return
    */
    Integer insert(JobRunLogEntity jobRunLogEntity);
}
