package com.fox.api.service.quartz;

import com.fox.api.dao.quartz.entity.QuartzJobParamEntity;

import java.util.List;

/**
 * 计划任务参数管理
 * @author lusongsong
 */
public interface QuartzJobParamService {
    /**
     * 添加参数
     * @param quartzJobParamEntity
     * @return
     */
    Integer insert(QuartzJobParamEntity quartzJobParamEntity);

    /**
     * 更新任务参数
     * @param quartzJobParamEntity
     * @return
     */
    Boolean update(QuartzJobParamEntity quartzJobParamEntity);

    /**
     * 删除任务参数
     * @param id
     * @return
     */
    Boolean delete(Integer id);

    /**
     * 获取任务参数列表
     * @param jobId
     * @return
     */
    List<QuartzJobParamEntity> getByJob(Integer jobId);

    /**
     * 获取任务执行参数
     * @param jobId
     * @return
     */
    List<? extends Object> getJobParams(Integer jobId);

    /**
     * 获取参数信息
     * @param id
     * @return
     */
    QuartzJobParamEntity getById(Integer id);
}
