package com.fox.api.schedule;

import com.fox.api.service.quartz.impl.QuartzServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 测试任务
 * @author lusongsong
 */
@Component
public class TestJob {
    private static final Logger logger = LoggerFactory.getLogger(TestJob.class);
    public void execute() throws Exception {
        logger.info("TestJob.execute()");
    }

    public void execute(String string) throws Exception {
        logger.info("TestJob.execute(String):" + string);
        Thread.sleep(10000);
    }

    public void execute(String string, Integer integer) throws Exception {
        logger.info("TestJob.testExecute(String, Integer):" + string + " " + integer);
        Thread.sleep(10000);
    }
}
