package com.fox.api.service.quartz.impl;

import com.fox.api.configuration.quartz.QuartzJobFactory;
import com.fox.api.dao.quartz.entity.QuartzJobEntity;
import com.fox.api.dao.quartz.entity.QuartzJobParamEntity;
import com.fox.api.dao.quartz.mapper.QuartzJobMapper;
import com.fox.api.enums.code.quartz.QuartzJobCode;
import com.fox.api.exception.self.ServiceException;
import com.fox.api.service.quartz.QuartzJobManageService;
import com.fox.api.service.quartz.QuartzJobParamService;
import com.fox.api.service.quartz.QuartzJobService;
import com.fox.api.util.redis.MainRedisUtil;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 计划任务执行管理
 * @author lusongsong
 */
@Service
public class QuartzJobManageImpl implements QuartzJobManageService {
    @Autowired
    private QuartzJobMapper quartzJobMapper;

    @Autowired
    private QuartzJobService quartzJobService;

    @Autowired
    private QuartzJobParamService quartzJobParamService;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private MainRedisUtil mainRedisUtil;

    /**
     * 获取任务唯一标识
     * @param quartzJobEntity
     * @return
     */
    private JobKey getJobKey(QuartzJobEntity quartzJobEntity) {
        return JobKey.jobKey(quartzJobEntity.getJobKey(), quartzJobEntity.getJobGroup());
    }

    @Override
    public Boolean startJob(Integer jobId) {
        try {
            QuartzJobEntity quartzJobEntity = this.quartzJobService.getById(jobId);
            List quartzJobParamList = this.quartzJobParamService.getJobParams(jobId);
            if (null == quartzJobEntity) {
                throw new ServiceException(QuartzJobCode.QUARTZ_JOB_NOT_FOUND);
            }
            JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class)
                    .withIdentity(quartzJobEntity.getJobKey(), quartzJobEntity.getJobGroup())
                    .build();
            String triggerKey = quartzJobEntity.getJobKey()+quartzJobEntity.getJobGroup();
            jobDetail.getJobDataMap().put("quartzJobEntity", quartzJobEntity);
            jobDetail.getJobDataMap().put("quartzJobParamList", quartzJobParamList);
            jobDetail.getJobDataMap().put("triggerKey", triggerKey);
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartzJobEntity.getCronExpr());
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(quartzJobEntity.getJobKey(), quartzJobEntity.getJobGroup())
                    .withSchedule(cronScheduleBuilder)
                    .build();

            mainRedisUtil.delete(triggerKey);
            Date date = scheduler.scheduleJob(jobDetail,cronTrigger);
            if (null != date) {
                quartzJobMapper.updateStatusById(quartzJobEntity.getId(), "running");
                return true;
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
        return false;
    }

    @Override
    public Boolean pauseJob(Integer jobId) {
        try {
            QuartzJobEntity quartzJobEntity = this.quartzJobService.getById(jobId);
            if (null == quartzJobEntity) {
                throw new ServiceException(QuartzJobCode.QUARTZ_JOB_NOT_FOUND);
            }
            scheduler.pauseJob(this.getJobKey(quartzJobEntity));
            quartzJobMapper.updateStatusById(quartzJobEntity.getId(), "paused");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
        return false;

    }

    @Override
    public Boolean resumeJob(Integer jobId) {
        try {
            QuartzJobEntity quartzJobEntity = this.quartzJobService.getById(jobId);
            if (null == quartzJobEntity) {
                throw new ServiceException(QuartzJobCode.QUARTZ_JOB_NOT_FOUND);
            }
            scheduler.resumeJob(this.getJobKey(quartzJobEntity));
            quartzJobMapper.updateStatusById(quartzJobEntity.getId(), "running");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
        return false;
    }

    @Override
    public Boolean stopJob(Integer jobId) {
        try {
            QuartzJobEntity quartzJobEntity = this.quartzJobService.getById(jobId);
            if (null == quartzJobEntity) {
                throw new ServiceException(QuartzJobCode.QUARTZ_JOB_NOT_FOUND);
            }
            scheduler.deleteJob(this.getJobKey(quartzJobEntity));
            quartzJobMapper.updateStatusById(quartzJobEntity.getId(), "deleted");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(1, e.getMessage());
        }
        return false;
    }

    @Override
    public void loadTotalQuartzJob() {
        Integer startId = 0;
        while (true) {
            List<QuartzJobEntity> list = quartzJobMapper.getLoadedJobList(startId, 10);
            if (null == list || 0 == list.size()) {
                break;
            }

            for (QuartzJobEntity quartzJobEntity : list) {
                startId = quartzJobEntity.getId();
                if (quartzJobEntity.getJobStatus().equals("running")) {
                    this.startJob(startId);
                } else {
                    quartzJobMapper.updateStatusById(quartzJobEntity.getId(), "deleted");
                }
            }
        }
    }
}
