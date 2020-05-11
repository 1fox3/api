package com.fox.api.annotation.aspect.service;

import com.fox.api.exception.self.ServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
/**
 * 服务层运行时将异常转为ServiceException
 * @author lusongsong
 */
public class ServiceExceptionAspect {
    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceExceptionAnt() {}

    @Around("serviceExceptionAnt()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        try {
            return proceedingJoinPoint.proceed();//有这一句，目标方法才会执行，不然目标方法无法执行
        } catch (ServiceException se) {
            throw se;
        } catch (Throwable throwable) {
            throw new ServiceException(1, throwable.getMessage());
        }
    }
}
