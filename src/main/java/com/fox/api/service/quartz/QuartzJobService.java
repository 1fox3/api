package com.fox.api.service.quartz;

import com.fox.api.dao.quartz.entity.QuartzJobEntity;

/**
 * 计划任务管理
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
     * 获取任务
     * @param jobId
     * @return
     */
    QuartzJobEntity getById(Integer jobId);

    /**
     * 启动任务
     * @param jobId
     * @return
     */
    Boolean startJob(Integer jobId);

    /**
     * 启动任务
     * @param quartzJobEntity
     * @return
     */
    Boolean startJob(QuartzJobEntity quartzJobEntity);

    /**
     * 暂停任务
     * @param jobId
     * @return
     */
    Boolean pauseJob(Integer jobId);

    /**
     * 暂停任务
     * @param quartzJobEntity
     * @return
     */
    Boolean pauseJob(QuartzJobEntity quartzJobEntity);

    /**
     * 继续运行
     * @param jobId
     * @return
     */
    Boolean resumeJob(Integer jobId);

    /**
     * 继续运行
     * @param quartzJobEntity
     * @return
     */
    Boolean resumeJob(QuartzJobEntity quartzJobEntity);

    /**
     * 继续运行
     * @param jobId
     * @return
     */
    Boolean deleteJob(Integer jobId);

    /**
     * 继续运行
     * @param quartzJobEntity
     * @return
     */
    Boolean deleteJob(QuartzJobEntity quartzJobEntity);

    /**
     * 加载所有计划任务
     */
    void loadTotalQuartzJob();
}
