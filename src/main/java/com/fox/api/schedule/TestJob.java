package com.fox.api.schedule;

import org.springframework.stereotype.Service;

/**
 * 测试任务
 * @author lusongsong
 */
@Service("testJob")
public class TestJob {
    public void execute(){
        System.out.println("TestJob.execute");
    }

    public void testExecute(){
        System.out.println("TestJob.testExecute");
    }
}
