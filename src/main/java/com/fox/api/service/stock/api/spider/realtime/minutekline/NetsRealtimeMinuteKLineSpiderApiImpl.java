package com.fox.api.service.stock.api.spider.realtime.minutekline;

import com.fox.api.entity.po.stock.api.StockRealtimeMinuteKLinePo;
import com.fox.api.entity.po.stock.api.StockRealtimeMinuteNodeDataPo;
import com.fox.api.service.stock.api.spider.StockRealtimeMinuteKLineSpiderApiService;
import com.fox.spider.stock.api.nets.NetsRealtimeMinuteKLineApi;
import com.fox.spider.stock.entity.po.nets.NetsRealtimeMinuteKLinePo;
import com.fox.spider.stock.entity.po.nets.NetsRealtimeMinuteNodeDataPo;
import com.fox.spider.stock.entity.vo.StockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 网易股票最新交易日分钟线图数据接口对接
 *
 * @author lusongsong
 * @date 2021/1/22 16:12
 */
@Service
@Primary
public class NetsRealtimeMinuteKLineSpiderApiImpl implements StockRealtimeMinuteKLineSpiderApiService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 网易股票最新交易日分钟线图数据接口
     */
    @Autowired
    NetsRealtimeMinuteKLineApi netsRealtimeMinuteKLineApi;

    /**
     * 股票最新交易日分钟线图数据
     *
     * @param stockVo
     * @return
     */
    @Override
    public StockRealtimeMinuteKLinePo realtimeMinuteKLine(StockVo stockVo) {
        try {
            return convertObj(netsRealtimeMinuteKLineApi.realtimeMinuteKLine(stockVo));
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
        return NetsRealtimeMinuteKLineApi.isSupport(stockMarket);
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
     * @param netsRealtimeMinuteKLinePo
     * @return
     */
    private StockRealtimeMinuteKLinePo convertObj(NetsRealtimeMinuteKLinePo netsRealtimeMinuteKLinePo) {
        if (null == netsRealtimeMinuteKLinePo || !(netsRealtimeMinuteKLinePo instanceof NetsRealtimeMinuteKLinePo)) {
            return null;
        }
        StockRealtimeMinuteKLinePo stockRealtimeMinuteKLinePo = new StockRealtimeMinuteKLinePo();
        BeanUtils.copyProperties(netsRealtimeMinuteKLinePo, stockRealtimeMinuteKLinePo, "klineData");
        List<NetsRealtimeMinuteNodeDataPo> netsRealtimeMinuteNodeDataPoList = netsRealtimeMinuteKLinePo.getKlineData();
        if (null != netsRealtimeMinuteNodeDataPoList && !netsRealtimeMinuteNodeDataPoList.isEmpty()) {
            List<StockRealtimeMinuteNodeDataPo> stockRealtimeMinuteNodeDataPoList =
                    new ArrayList<>(netsRealtimeMinuteNodeDataPoList.size());
            for (NetsRealtimeMinuteNodeDataPo netsRealtimeMinuteNodeDataPo : netsRealtimeMinuteNodeDataPoList) {
                if (null == netsRealtimeMinuteNodeDataPo) {
                    continue;
                }
                StockRealtimeMinuteNodeDataPo stockRealtimeMinuteNodeDataPo = new StockRealtimeMinuteNodeDataPo();
                BeanUtils.copyProperties(netsRealtimeMinuteNodeDataPo, stockRealtimeMinuteNodeDataPo);
                stockRealtimeMinuteNodeDataPoList.add(stockRealtimeMinuteNodeDataPo);
            }
            stockRealtimeMinuteKLinePo.setNodeCount(stockRealtimeMinuteNodeDataPoList.size());
            stockRealtimeMinuteKLinePo.setKlineData(stockRealtimeMinuteNodeDataPoList);
        }
        return stockRealtimeMinuteKLinePo;
    }
}
