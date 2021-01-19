package com.fox.api.service.stock.api.request.impl;

import com.fox.api.service.stock.api.spider.StockSpiderApiBaseInterface;
import com.fox.api.util.ApplicationContextUtil;
import com.fox.api.util.IntUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接口服务基类
 *
 * @author lusongsong
 * @date 2021/1/15 17:39
 */
public class StockApiServiceBaseImpl {
    /**
     * 主
     */
    public static final int CHOOSE_METHOD_PRIMARY = 1;
    /**
     * 随机
     */
    public static final int CHOOSE_METHOD_RANDOM = 2;
    /**
     * 轮询
     */
    public static final int CHOOSE_METHOD_POLL = 3;
    /**
     * 权重
     */
    public static final int CHOOSE_METHOD_WEIGHT = 4;
    /**
     * bean选择方案
     */
    private int chooseMethod = CHOOSE_METHOD_PRIMARY;
    /**
     * 当前位置
     */
    private int currentPos = 0;
    /**
     * 当前权重计数
     */
    private int currentWeightCount = 0;
    /**
     * 所有实现类
     */
    private LinkedHashMap<String, StockSpiderApiBaseInterface> beanMap = null;
    /**
     * 无用的实现类
     */
    private Map<String, StockSpiderApiBaseInterface> uselessBeanMap = null;
    /**
     * 无用的实现类的失效时间
     */
    private Map<String, Long> uselessTimeMap = null;

    /**
     * 根据全限定类名去寻找一个spring管理的bean实例
     *
     * @param clazz
     * @return
     */
    private <T extends StockSpiderApiBaseInterface> void getSpiderBean(Class<T> clazz) {
        beanMap = (LinkedHashMap<String, StockSpiderApiBaseInterface>) ApplicationContextUtil.getBeansOfType(clazz);
    }

    /**
     * 设置选择方式
     *
     * @param method
     */
    public void setChooseMethod(int method) {
        chooseMethod = method;
    }

    /**
     * 根据规则获取实例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends StockSpiderApiBaseInterface> T getBean(Class<T> clazz) {
        if (null == beanMap) {
            getSpiderBean(clazz);
        }
        if (null == beanMap || beanMap.isEmpty()) {
            return null;
        }
        //选择之前将失效的bean重新启用
        reuse();
        //按照不同的选取方式计算位置
        switch (chooseMethod) {
            case CHOOSE_METHOD_RANDOM:
                getPosByRandom();
                break;
            case CHOOSE_METHOD_POLL:
                getPosByPool();
                break;
            case CHOOSE_METHOD_WEIGHT:
                getPosByWeight();
                break;
            default:
                getPosByPrimary();
        }
        currentPos %= beanMap.values().size();
        searchVerifyBean();
        if (-1 == currentPos) {
            return null;
        }
        return (T) beanMap.values().toArray()[currentPos];
    }

    /**
     * 获取主实例
     *
     * @return
     */
    private void getPosByPrimary() {
        currentPos = 0;
    }

    /**
     * 随机获取实例位置
     *
     * @return
     */
    private void getPosByRandom() {
        currentPos += IntUtil.random();
    }

    /**
     * 轮询获取实例位置
     *
     * @return
     */
    private void getPosByPool() {
        currentPos += 1;
    }

    /**
     * 根据权重获取实例位置
     *
     * @return
     */
    private void getPosByWeight() {
        currentWeightCount += 1;
        int weight = ((StockSpiderApiBaseInterface) (beanMap.values().toArray()[currentPos])).weight();
        ;
        if (currentWeightCount > weight) {
            currentPos += 1;
            currentWeightCount = 0;
        }
    }

    /**
     * 设置不可用
     *
     * @param beanName
     * @param time
     */
    public void setUseless(String beanName, Long time) {
        if (null != beanMap && beanMap.containsKey(beanName)) {
            if (null == uselessBeanMap) {
                uselessBeanMap = new HashMap<>(1);
            }
            uselessBeanMap.put(beanName, beanMap.get(beanName));
            uselessTimeMap.put(beanName, System.currentTimeMillis() + time);
            beanMap.remove(beanName);
        }
    }

    /**
     * 失效的bean重新启用
     */
    private void reuse() {
        if (null != uselessTimeMap && !uselessTimeMap.isEmpty()) {
            long currentTime = System.currentTimeMillis();
            for (String beanName : uselessTimeMap.keySet()) {
                if (uselessTimeMap.get(beanName) <= currentTime) {
                    beanMap.put(beanName, uselessBeanMap.get(beanName));
                    uselessBeanMap.remove(beanName);
                    uselessTimeMap.remove(beanName);
                }
            }
        }
    }

    /**
     * 判断bean是否适合
     *
     * @return
     */
    public boolean verifyBean(Object bean) {
        return true;
    }

    /**
     * 判断bean是否适合
     */
    public void searchVerifyBean() {
        int currPos = currentPos;
        while (true) {
            if (verifyBean(beanMap.values().toArray()[currPos])) {
                currentPos = currPos;
                break;
            }
            currPos += 1;
            currPos %= beanMap.values().size();
            if (currPos == currentPos) {
                currentPos = -1;
                break;
            }
        }
    }
}
