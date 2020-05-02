package com.fox.api.configuration.quartz;

import com.fox.api.dao.quartz.entity.QuartzJobEntity;
import com.fox.api.service.quartz.QuartzService;
import com.fox.api.util.redis.MainRedisUtil;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 计划任务管理工厂
 * @author lusongsong
 */
@Service
public class QuartzJobFactory extends QuartzJobBean {
    @Autowired
    QuartzService quartzService;

    @Autowired
    private MainRedisUtil mainRedisUtil;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        QuartzJobEntity object = (QuartzJobEntity) jobExecutionContext.getMergedJobDataMap().get("quartzJobEntity");
        List quartzJobParamList = (List) jobExecutionContext.getMergedJobDataMap().get("quartzJobParamList");
        String triggerKey = (String) jobExecutionContext.getMergedJobDataMap().get("triggerKey");

        //如果使用锁,则在缓存中设置标识
        if (1 == object.getUseLock()) {
            if (null != mainRedisUtil.get(triggerKey)) {
                return;
            }

            mainRedisUtil.set(triggerKey, "1");
        }

        if (object.getMethodName() == null || object.getMethodName().equals("")) {
            quartzService.executeTask(object.getBeanName());
        } else if (0 < quartzJobParamList.size()) {
            quartzService.executeTask(object.getBeanName(),object.getMethodName(), quartzJobParamList);
        } else {
            quartzService.executeTask(object.getBeanName(),object.getMethodName());
        }

        //任务完成后,删除锁标识
        if (1 == object.getUseLock()) {
            mainRedisUtil.delete(triggerKey);
        }
    }
}
