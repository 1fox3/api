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
     * 获取已加载的任务列表
     * @param startId
     * @param num
     * @return
     */
    List<QuartzJobEntity> getLoadedJobList(Integer startId, Integer num);

    /**
     * 根据分组获取任务列表
     * @param jobGroup
     * @return
     */
    List<QuartzJobEntity> getListByGroup(String jobGroup);

    /**
     * 更新任务
     * @param quartzJobEntity
     * @return
     */
    Boolean update(QuartzJobEntity quartzJobEntity);
}
