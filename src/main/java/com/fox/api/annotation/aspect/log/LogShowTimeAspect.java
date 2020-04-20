package com.fox.api.annotation.aspect.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Aspect
@Component
/**
 * @author lusongsong
 */
public class LogShowTimeAspect {

    @Pointcut("@annotation(com.fox.api.annotation.aspect.log.LogShowTimeAnt)")
    public void logShowTimePointcut() {}

    /**
     * 显示时间
     * @param joinPoint
     */
    protected void showTime(JoinPoint joinPoint, String breakpoint) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Logger logger = LoggerFactory.getLogger(className);
        logger.info(className + ":" + methodName + ":" + breakpoint + ":" + df.format(System.currentTimeMillis()));
    }

    @Before("logShowTimePointcut()")
    public void before(JoinPoint joinPoint){
        this.showTime(joinPoint, "start");
    }

    @After("logShowTimePointcut()")
    public void after(JoinPoint joinPoint) {
        this.showTime(joinPoint, "end");
    }
}
