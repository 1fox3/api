package com.fox.api.service.stock.api.spider.realtime.minutekline;

import com.fox.api.entity.po.stock.api.StockRealtimeMinuteKLinePo;
import com.fox.api.entity.po.stock.api.StockRealtimeMinuteNodeDataPo;
import com.fox.api.service.stock.api.spider.StockRealtimeMinuteKLineSpiderApiService;
import com.fox.spider.stock.api.tencent.TencentRealtimeMinuteKLineApi;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimeMinuteKLinePo;
import com.fox.spider.stock.entity.po.tencent.TencentRealtimeMinuteNodeDataPo;
import com.fox.spider.stock.entity.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 腾讯股票最新交易日分钟线图数据接口对接
 *
 * @author lusongsong
 * @date 2021/1/22 16:28
 */
@Service
public class TencentRealtimeMinuteKLineSpierApiImpl implements StockRealtimeMinuteKLineSpiderApiService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 腾讯股票近5个交易日分钟线图数据接口
     */
    @Autowired
    TencentRealtimeMinuteKLineApi tencentRealtimeMinuteKLineApi;

    /**
     * 股票最新交易日分钟线图数据
     *
     * @param stockVo
     * @return
     */
    @Override
    public StockRealtimeMinuteKLinePo realtimeMinuteKLine(StockVo stockVo) {
        try {
            return convertObj(tencentRealtimeMinuteKLineApi.realtimeMinuteKLine(stockVo));
        } catch (Exception e) {
            logger.error(stockVo.toString(), e);
        }
        return null;
    }

    /**
     * 是否支持该证券所
     *
     * @param stockMarket
     * @return
     */
    @Override
    public boolean isSupport(int stockMarket) {
        return TencentRealtimeMinuteKLineApi.isSupport(stockMarket);
    }

    /**
     * 权重
     *
     * @return
     */
    @Override
    public int weight() {
        return 1;
    }

    /**
     * 对象转换
     *
     * @param tencentRealtimeMinuteKLinePo
     * @return
     */
    private StockRealtimeMinuteKLinePo convertObj(TencentRealtimeMinuteKLinePo tencentRealtimeMinuteKLinePo) {
        if (null == tencentRealtimeMinuteKLinePo
                || !(tencentRealtimeMinuteKLinePo instanceof TencentRealtimeMinuteKLinePo)) {
            return null;
        }
        StockRealtimeMinuteKLinePo stockRealtimeMinuteKLinePo = new StockRealtimeMinuteKLinePo();
        BeanUtils.copyProperties(
                tencentRealtimeMinuteKLinePo,
                stockRealtimeMinuteKLinePo,
                "klineData"
        );
        List<TencentRealtimeMinuteNodeDataPo> tencentRealtimeMinuteNodeDataPoList =
                tencentRealtimeMinuteKLinePo.getKlineData();
        if (null != tencentRealtimeMinuteNodeDataPoList && !tencentRealtimeMinuteNodeDataPoList.isEmpty()) {
            List<StockRealtimeMinuteNodeDataPo> stockRealtimeMinuteNodeDataPoList =
                    new ArrayList<>(tencentRealtimeMinuteNodeDataPoList.size());
            for (TencentRealtimeMinuteNodeDataPo tencentRealtimeMinuteNodeDataPo : tencentRealtimeMinuteNodeDataPoList) {
                if (null == tencentRealtimeMinuteNodeDataPo) {
                    continue;
                }
                StockRealtimeMinuteNodeDataPo stockRealtimeMinuteNodeDataPo = new StockRealtimeMinuteNodeDataPo();
                BeanUtils.copyProperties(tencentRealtimeMinuteNodeDataPo, stockRealtimeMinuteNodeDataPo);
                stockRealtimeMinuteNodeDataPoList.add(stockRealtimeMinuteNodeDataPo);
            }
            stockRealtimeMinuteKLinePo.setNodeCount(stockRealtimeMinuteNodeDataPoList.size());
            stockRealtimeMinuteKLinePo.setKlineData(stockRealtimeMinuteNodeDataPoList);
        }
        return stockRealtimeMinuteKLinePo;
    }
}
