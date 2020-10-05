package com.fox.api.service.admin.impl;

import com.fox.api.dao.admin.entity.DateTypeEntity;
import com.fox.api.dao.admin.mapper.DateTypeMapper;
import com.fox.api.entity.dto.http.HttpResponseDto;
import com.fox.api.service.admin.DateType;
import com.fox.api.util.DateUtil;
import com.fox.api.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lusongsong
 * @date 2020/10/5 16:53
 */
@Service
public class DateTypeImpl implements DateType {
    @Autowired
    DateTypeMapper dateTypeMapper;

    /**
     * 未知
     */
    public static final Integer DATE_TYPE_UNKNOWN = 0;
    /**
     * 工作日
     */
    public static final Integer DATE_TYPE_WORKDAY = 1;
    /**
     * 周末
     */
    public static final Integer DATE_TYPE_WEEKEND = 2;
    /**
     * 假期
     */
    public static final Integer DATE_TYPE_HOLIDAY = 3;
    /**
     * 调班
     */
    public static final Integer DATE_TYPE_TRANSFER = 4;

    /**
     * 请求外部地址获取日期类型
     * @param dt
     * @return
     */
    private Integer getTypeByUrl(String dt) {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.setUrl("http://tool.bitefu.net/jiari/");
        httpUtil.setParam("d", dt);
        try {
            HttpResponseDto httpResponse = httpUtil.request();
            String content = httpResponse.getContent();
            Integer type = Integer.valueOf(content.trim());
            switch (type) {
                case 2:
                    return DATE_TYPE_HOLIDAY;
                case 1:
                    return DATE_TYPE_WEEKEND;
                case 0:
                    Integer dayInWeekNum = DateUtil.getDayInWeekNum(dt, DateUtil.DATE_FORMAT_1);
                    if (6 == dayInWeekNum || 0 == dayInWeekNum) {
                        return DATE_TYPE_TRANSFER;
                    }
                    return DATE_TYPE_WORKDAY;
                default:
                    return DATE_TYPE_UNKNOWN;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DATE_TYPE_UNKNOWN;
        }
    }

    /**
     * 查询数据库获取日期类型
     * @param dt
     * @return
     */
    private Integer getTypeByDB(String dt) {
        DateTypeEntity dateTypeEntity = dateTypeMapper.getByDate(dt);
        return null != dateTypeEntity ? dateTypeEntity.getType() : null;
    }

    /**
     * 将日期类型保存到数据库中
     * @param dt
     * @param dateType
     */
    private void saveTypeByDB(String dt, Integer dateType) {
        DateTypeEntity dateTypeEntity = dateTypeMapper.getByDate(dt);
        if (null == dateTypeEntity) {
            dateTypeEntity = new DateTypeEntity();
        }
        dateTypeEntity.setDt(dt);
        dateTypeEntity.setType(dateType);
        if (null == dateTypeEntity.getId()) {
            dateTypeMapper.insert(dateTypeEntity);
        } else {
            dateTypeMapper.update(dateTypeEntity);
        }
    }

    /**
     * 根据日期获取日期类型
     *
     * @param dt
     * @return
     */
    @Override
    public Integer getByDate(String dt) {
        Integer dateType;
        dateType = getTypeByDB(dt);
        if (null == dateType) {
            dateType = getTypeByUrl(dt);
            if (null != dateType) {
                saveTypeByDB(dt, dateType);
            }
        }
        return null == dateType ? DATE_TYPE_UNKNOWN : dateType;
    }
}
