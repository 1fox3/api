package com.fox.api.service.quartz;

import com.fox.api.dao.quartz.entity.QuartzJobEntity;

import java.util.List;

/**
 * 计划任务信息管理
 * @author lusongsong
 */
public interface QuartzJobService {
    /**
     * 添加计划任务
     * @param quartzJobEntity
     * @return
     */
    Integer insert(QuartzJobEntity quartzJobEntity);

    /**
     * 更新任务
     * @param quartzJobEntity
     * @return
     */
    Boolean updateJob(QuartzJobEntity quartzJobEntity);

    /**
     * 删除任务
     * @param jobId
     * @return
     */
    Boolean deleteJob(Integer jobId);

    /**
     * 获取任务
     * @param jobId
     * @return
     */
    QuartzJobEntity getById(Integer jobId);

    /**
     * 根据分组获取数据
     * @param jobGroup
     * @return
     */
    List<QuartzJobEntity> getListByGroup(String jobGroup);
}
