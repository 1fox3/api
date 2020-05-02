package com.fox.api.service.quartz.impl;

import com.fox.api.dao.quartz.entity.QuartzJobEntity;
import com.fox.api.dao.quartz.mapper.QuartzJobMapper;
import com.fox.api.enums.code.quartz.QuartzJobCode;
import com.fox.api.exception.self.ServiceException;
import com.fox.api.service.quartz.QuartzJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 计划任务管理
 * @author lusongsong
 */
@Service
public class QuartzJobImpl implements QuartzJobService {
    private static final Logger logger = LoggerFactory.getLogger(QuartzJobImpl.class);

    /**
     * 允许删除的任务状态
     */
    private static List<String> allowDeleteStatusList = new ArrayList<>(Arrays.asList("init", "deleted"));

    @Autowired
    private QuartzJobMapper quartzJobMapper;

    /**
     * 添加任务信息
     * @param quartzJobEntity
     * @return
     * @throws ServiceException
     */
    @Override
    public Integer insert(QuartzJobEntity quartzJobEntity) throws ServiceException {
        try {
            quartzJobEntity.setJobStatus("init");
            Integer insertResult = quartzJobMapper.insert(quartzJobEntity);
            if (1 == insertResult) {
                return quartzJobEntity.getId();
            }
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
        return null;
    }

    /**
     * 更新任务信息
     * @param quartzJobEntity
     * @return
     */
    @Override
    public Boolean updateJob(QuartzJobEntity quartzJobEntity) {
        try {
            return quartzJobMapper.update(quartzJobEntity);
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
    }

    /**
     * 删除任务信息
     * @param jobId
     * @return
     */
    @Override
    public Boolean deleteJob(Integer jobId) {
        try {
            QuartzJobEntity quartzJobEntity = this.getById(jobId);
            if (null == quartzJobEntity
                    || !QuartzJobImpl.allowDeleteStatusList.contains(quartzJobEntity.getJobStatus())) {
                throw new ServiceException(QuartzJobCode.QUARTZ_JOB_DENY_DELETE);
            }
            return quartzJobMapper.updateStatusById(jobId, "useless");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
    }

    /**
     * 获取任务信息
     * @param jobId
     * @return
     */
    @Override
    public QuartzJobEntity getById(Integer jobId) {
        try {
            QuartzJobEntity quartzJobEntity = quartzJobMapper.getById(jobId);
            if (null == quartzJobEntity) {
                throw new ServiceException(QuartzJobCode.QUARTZ_JOB_NOT_FOUND);
            }
            return quartzJobEntity;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
    }

    /**
     * 根据任务分组获取任务列表
     * @param jobGroup
     * @return
     */
    @Override
    public List<QuartzJobEntity> getListByGroup(String jobGroup) {
        try {
            return quartzJobMapper.getListByGroup(jobGroup);
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
    }
}
