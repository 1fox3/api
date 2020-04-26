package com.fox.api.dao.quartz.mapper;

import com.fox.api.annotation.mapper.QuartzMapperConfig;
import com.fox.api.dao.quartz.entity.QuartzJobEntity;

import java.util.List;

/**
 * 计划任务管理
 * @author lusongsong
 */
@QuartzMapperConfig
public interface QuartzJobMapper {
    /**
     * 添加计划任务
     * @param quartzJobEntity
     * @return
     */
    Integer insert(QuartzJobEntity quartzJobEntity);

    /**
     * 获取任务信息
     * @param id
     * @return
     */
    QuartzJobEntity getById(Integer id);

    /**
     * 更新状态
     * @param id
     * @param jobStatus
     * @return
     */
    Boolean updateStatusById(Integer id, String jobStatus);

    /**
     * 根据任务状态获取任务列表
     * @param jobStatus
     * @param startId
     * @param num
     * @return
     */
    List<QuartzJobEntity> getListByStatus(String jobStatus, Integer startId, Integer num);
}
