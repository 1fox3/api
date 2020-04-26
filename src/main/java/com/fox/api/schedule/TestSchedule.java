package com.fox.api.schedule;

import com.fox.api.schedule.stock.StockBaseSchedule;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 测试任务
 * @author lusongsong
 */
public class TestSchedule  extends StockBaseSchedule {
    @Scheduled(cron = "*/2 * * * * ?")
    public void execute(){
        System.out.println("TestSchedule.execute");
    }

    @Scheduled(cron = "*/2 * * * * ?")
    public void testExecute(){
        System.out.println("TestSchedule.testExecute");
    }
}
