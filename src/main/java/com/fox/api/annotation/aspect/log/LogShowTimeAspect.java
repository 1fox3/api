package com.fox.api.annotation.aspect.log;

import com.fox.api.dao.quartz.entity.JobRunLogEntity;
import com.fox.api.dao.quartz.mapper.JobRunLogMapper;
import com.fox.api.util.DateUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

/**
 * 记录执行时间
 *
 * @author lusongsong
 * @date 2020/04/14 13:53
 */
@Aspect
@Component
public class LogShowTimeAspect {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    JobRunLogMapper jobRunLogMapper;

    @Pointcut("@annotation(com.fox.api.annotation.aspect.log.LogShowTimeAnt)")
    public void logShowTimePointcut() {
    }

    /**
     * 显示时间
     *
     * @param joinPoint
     */
    protected void showTime(JoinPoint joinPoint, String breakpoint) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        JobRunLogEntity jobRunLogEntity = new JobRunLogEntity();
        jobRunLogEntity.setBeanName(className);
        jobRunLogEntity.setMethodName(methodName);
        jobRunLogEntity.setLogTime(DateUtil.getCurrentTime());
        jobRunLogEntity.setInfo(breakpoint);
        try {
            jobRunLogMapper.insert(jobRunLogEntity);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(jobRunLogEntity.toString());
        }

    }

    @Before("logShowTimePointcut()")
    public void before(JoinPoint joinPoint) {
        this.showTime(joinPoint, "start");
    }

    @After("logShowTimePointcut()")
    public void after(JoinPoint joinPoint) {
        this.showTime(joinPoint, "end");
    }

    @AfterThrowing(value = "logShowTimePointcut()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        this.showTime(joinPoint, null == ex.getMessage() ? ex.toString() : ex.getMessage());
    }
}
