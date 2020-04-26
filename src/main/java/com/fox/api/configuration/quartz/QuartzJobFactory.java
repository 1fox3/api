package com.fox.api.configuration.quartz;

import com.fox.api.dao.quartz.entity.QuartzJobEntity;
import com.fox.api.service.quartz.QuartzService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

/**
 * 计划任务管理工厂
 * @author lusongsong
 */
@Service
public class QuartzJobFactory extends QuartzJobBean {
    @Autowired
    QuartzService quartzService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        QuartzJobEntity object = (QuartzJobEntity) jobExecutionContext.getMergedJobDataMap().get("scheduleJobEntity");
        if (object.getMethodName() == null || object.getMethodName().equals("")) {
            quartzService.executeTask(object.getBeanName());
        } else {
            quartzService.executeTask(object.getBeanName(),object.getMethodName());
        }
    }
}
