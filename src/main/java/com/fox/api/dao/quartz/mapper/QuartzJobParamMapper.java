package com.fox.api.dao.quartz.mapper;

import com.fox.api.annotation.mapper.QuartzMapperConfig;
import com.fox.api.dao.quartz.entity.QuartzJobParamEntity;

import java.util.List;

/**
 * 计划任务参数
 * @author lusongsong
 */
@QuartzMapperConfig
public interface QuartzJobParamMapper {
    /**
     * 添加任务参数
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
    List<QuartzJobParamEntity> getByJobId(Integer jobId);

    /**
     * 获取任务参信息
     * @param id
     * @return
     */
    QuartzJobParamEntity getById(Integer id);
}
