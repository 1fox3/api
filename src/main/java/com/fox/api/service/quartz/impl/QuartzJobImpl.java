package com.fox.api.service.quartz.impl;

import com.fox.api.configuration.quartz.QuartzJobFactory;
import com.fox.api.dao.quartz.entity.QuartzJobEntity;
import com.fox.api.dao.quartz.mapper.QuartzJobMapper;
import com.fox.api.service.quartz.QuartzJobService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 计划任务管理
 * @author lusongsong
 */
@Service("quartzJobService")
public class QuartzJobImpl implements QuartzJobService {
    private static final Logger logger = LoggerFactory.getLogger(QuartzJobImpl.class);

    @Autowired
    private QuartzJobMapper quartzJobMapper;

    @Autowired
    private Scheduler scheduler;

    /**
     * 获取任务唯一标识
     * @param quartzJobEntity
     * @return
     */
    private JobKey getJobKey(QuartzJobEntity quartzJobEntity) {
        return JobKey.jobKey(quartzJobEntity.getJobKey(), quartzJobEntity.getJobGroup());
    }

    @Override
    public Integer insert(QuartzJobEntity quartzJobEntity) {
        TriggerKey triggerKey = TriggerKey.triggerKey(quartzJobEntity.getJobKey(), quartzJobEntity.getJobGroup());

        try {
            CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (null != cronTrigger) {
                return null;
            }
        } catch (SchedulerException e) {
            logger.error(e.getStackTrace().toString());
            return null;
        }

        quartzJobEntity.setJobStatus("init");
        Integer insertResult = quartzJobMapper.insert(quartzJobEntity);
        if (1 == insertResult) {
            //启动任务
            this.startJob(quartzJobEntity);
            return quartzJobEntity.getId();
        }
        return null;
    }

    @Override
    public QuartzJobEntity getById(Integer jobId) {
        return quartzJobMapper.getById(jobId);
    }

    /**
     * 启动任务
     * @param quartzJobEntity
     * @return
     */
    @Override
    public Boolean startJob(QuartzJobEntity quartzJobEntity) {
        JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class)
                .withIdentity(quartzJobEntity.getJobKey(), quartzJobEntity.getJobGroup())
                .build();
        jobDetail.getJobDataMap().put("scheduleJobEntity", quartzJobEntity);
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartzJobEntity.getCronExpr());
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(quartzJobEntity.getJobKey(), quartzJobEntity.getJobGroup())
                .withSchedule(cronScheduleBuilder).build();

        try {
            Date date = scheduler.scheduleJob(jobDetail,cronTrigger);
            if (null != date) {
                quartzJobMapper.updateStatusById(quartzJobEntity.getId(), "running");
                return true;
            }
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     * 启动任务
     * @param jobId
     * @return
     */
    @Override
    public Boolean startJob(Integer jobId) {
        QuartzJobEntity quartzJobEntity = this.getById(jobId);
        if (null == quartzJobEntity) {
            return false;
        }
        return this.startJob(quartzJobEntity);
    }

    /**
     * 暂停任务
     * @param jobId
     * @return
     */
    @Override
    public Boolean pauseJob(Integer jobId) {
        QuartzJobEntity quartzJobEntity = this.getById(jobId);
        if (null == quartzJobEntity) {
            return false;
        }
        return this.pauseJob(quartzJobEntity);
    }

    /**
     * 暂停任务
     * @param quartzJobEntity
     * @return
     */
    @Override
    public Boolean pauseJob(QuartzJobEntity quartzJobEntity) {
        try {
            scheduler.pauseJob(this.getJobKey(quartzJobEntity));
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }

        quartzJobMapper.updateStatusById(quartzJobEntity.getId(), "paused");
        return true;
    }

    /**
     * 继续运行
     * @param jobId
     * @return
     */
    @Override
    public Boolean resumeJob(Integer jobId) {
        QuartzJobEntity quartzJobEntity = this.getById(jobId);
        if (null == quartzJobEntity) {
            return false;
        }
        return this.resumeJob(quartzJobEntity);
    }

    /**
     * 继续运行
     * @param quartzJobEntity
     * @return
     */
    @Override
    public Boolean resumeJob(QuartzJobEntity quartzJobEntity) {
        try {
            scheduler.resumeJob(this.getJobKey(quartzJobEntity));
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }

        quartzJobMapper.updateStatusById(quartzJobEntity.getId(), "running");
        return true;
    }

    /**
     * 删除任务
     * @param jobId
     * @return
     */
    @Override
    public Boolean deleteJob(Integer jobId) {
        QuartzJobEntity quartzJobEntity = this.getById(jobId);
        if (null == quartzJobEntity) {
            return false;
        }
        return this.deleteJob(quartzJobEntity);
    }

    /**
     * 删除任务
     * @param quartzJobEntity
     * @return
     */
    @Override
    public Boolean deleteJob(QuartzJobEntity quartzJobEntity) {
        try {
            scheduler.deleteJob(this.getJobKey(quartzJobEntity));
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }

        quartzJobMapper.updateStatusById(quartzJobEntity.getId(), "deleted");
        return true;
    }

    /**
     * 加载所有正在运行的任务
     */
    @Override
    public void loadTotalQuartzJob() {
        Integer startId = 0;
        while (true) {
            List<QuartzJobEntity> list = quartzJobMapper.getListByStatus("running", startId, 1);
            if (null == list || 0 == list.size()) {
                break;
            }

            for (QuartzJobEntity quartzJobEntity : list) {
                startId = quartzJobEntity.getId();
                this.startJob(quartzJobEntity);
            }
        }
    }

}
