package com.fox.api.schedule.stock;

import com.fox.api.annotation.aspect.log.LogShowTimeAnt;
import com.fox.api.constant.stock.StockTableDtConst;
import com.fox.api.dao.stock.StockTableBackMapper;
import com.fox.api.dao.stock.entity.StockTableDtEntity;
import com.fox.api.service.stock.StockTableDtService;
import com.fox.api.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 股票数据备份任务
 *
 * @author lusongsong
 * @date 2020/12/14 13:51
 */
@Component
public class StockTableBackSchedule {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 单次备份数据条数
     */
    private static final Integer BAK_ONCE_LIMIT = 100000;
    /**
     * 数据表天信息表
     */
    @Autowired
    StockTableDtService stockTableDtService;
    /**
     * 数据表备份数据库操作类
     */
    @Autowired
    StockTableBackMapper stockTableBackMapper;

    /**
     * 股票数据备份
     *
     * @param tableId
     * @param diffDay
     */
    @LogShowTimeAnt
    public void syncStockTableBak(Integer tableId, Integer diffDay) {
        if (null == tableId || null == diffDay) {
            return;
        }

        if (!StockTableDtConst.TABLE_DT_MAP.containsKey(tableId)) {
            return;
        }

        String table = StockTableDtConst.TABLE_DT_MAP.get(tableId);
        if (null == table || table.isEmpty()) {
            return;
        }

        String dt = DateUtil.getRelateDate(0, 0, -diffDay, DateUtil.DATE_FORMAT_1);
        System.out.println(dt);
        if (null == dt || dt.isEmpty()) {
            return;
        }

        try {
            //创建备份表
            stockTableBackMapper.createBakTable(table);

            //判断日期
            StockTableDtEntity stockTableDtEntity = new StockTableDtEntity();
            stockTableDtEntity.setTable(tableId);
            stockTableDtEntity.setType(StockTableDtConst.TYPE_DEFAULT);
            List<String> dtList = stockTableDtService.getByType(stockTableDtEntity);
            if (null == dtList || dtList.isEmpty()) {
                return;
            }

            for (String dataDt : dtList) {
                if (DateUtil.compare(dataDt, dt, DateUtil.DATE_FORMAT_1) > 0) {
                    continue;
                }
                //备份数据
                while (true) {
                    stockTableBackMapper.bakData(table, dataDt, BAK_ONCE_LIMIT);
                    Integer bakCount = stockTableBackMapper.clearOriData(table, dataDt, BAK_ONCE_LIMIT);
                    if (!BAK_ONCE_LIMIT.equals(bakCount)) {
                        break;
                    }
                }
                stockTableDtEntity.setDt(dataDt);
                stockTableDtService.setBak(stockTableDtEntity);
            }

            stockTableBackMapper.optimizeOriTable(table);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
