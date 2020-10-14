package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.dao.quartz.mapper.JobRunLogMapper;
import com.fox.api.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 股票相关日志清理
 * @author lusongsong
 * @date 2020/10/13 14:49
 */
@Component
public class StockLogClearSchedule {
    @Autowired
    JobRunLogMapper jobRunLogMapper;

    /**
     * 清除脚本执行记录
     */
    @LogShowTimeAnt
    public void jobRunLogClear() {
        String clearTime = DateUtil.getRelateDate(0, 0, -7, DateUtil.TIME_FORMAT_1);
        jobRunLogMapper.delete(clearTime);
        jobRunLogMapper.optimize();
    }
}
