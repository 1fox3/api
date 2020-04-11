package com.fox.api.util.redis;

import com.fox.api.entity.po.third.stock.StockRealtimeLinePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class RedisUtil {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取缓存模板
     * @return
     */
    public abstract RedisTemplate getRedisTemplate();

    /**
     * 记录人异常日志
     * @param e
     */
    private void logThrowable(Throwable e) {
        logger.error("redis操作失败", e);
    }

    /**
     * 指定缓存失败时间
     * @param key
     * @param time
     * @return
     */
    public boolean expire(Object key, Long time) {
        try {
            if (time > 0) {
                this.getRedisTemplate().expire(key.toString(), time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 获取过期时间
     * @param key
     * @return
     */
    public Long getExpire(Object key) {
        return this.getRedisTemplate().getExpire(key.toString(), TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public boolean hasKey(Object key) {
        try {
            return this.getRedisTemplate().hasKey(key.toString());
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 删除key
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public Long delete(Object... key) {
        if (null != key && key.length > 0) {
            if (1 == key.length) {
                return this.getRedisTemplate().delete(key[0].toString()) ? Long.valueOf(1) : Long.valueOf(-1);
            } else {
                List<String> keyList = new LinkedList<>();
                for (Object object : key) {
                    keyList.add(object.toString());
                }
                return this.getRedisTemplate().delete(keyList);
            }
        }
        return Long.valueOf(-1);
    }

    /**
     * 获取字符串缓存
     * @param key
     * @return
     */
    public Object get(Object key) {
        return null == key ? null : this.getRedisTemplate().opsForValue().get(key);
    }

    /**
     * 字符串类型缓存
     * @param key
     * @param value
     * @return
     */
    public boolean set(Object key, Object value) {
        try {
            this.getRedisTemplate().opsForValue().set(key, value);
            return true;
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 字符串类型缓存并设置时间
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public boolean set(Object key, Object value, Long seconds) {
        try {
            if (seconds > 0) {
                this.getRedisTemplate().opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
                return true;
            } else {
                return set(key, value);
            }
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 逐一递增
     * @param key
     * @return
     */
    public Long increment(Object key) {
        return this.increment(key, Long.valueOf(1));
    }

    /**
     * 递增
     * @param key
     * @param delta
     * @return
     */
    public Long increment(Object key, Long delta) {
        if (delta < 0) {
            throw new RuntimeException("redis缓存递增因子必须大于0");
        }
        return this.getRedisTemplate().opsForValue().increment(key, delta);
    }

    /**
     * 逐一递减
     * @param key
     * @return
     */
    public Long decrement(Object key) {
        return this.decrement(key, Long.valueOf(1));
    }

    /**
     * 递减
     * @param key
     * @param delta
     * @return
     */
    public Long decrement(Object key, Long delta) {
        if (delta < 0) {
            throw new RuntimeException("redis缓存递减因子必须大于0");
        }
        return this.getRedisTemplate().opsForValue().decrement(key, delta);
    }

    /**
     * HashGet
     * @param hash
     * @param key
     * @return
     */
    public Object hGet(Object hash, Object key) {
        return this.getRedisTemplate().opsForHash().get(hash, key);
    }

    /**
     * 获取hash对应的所有键值
     * @param hash
     * @return
     */
    public Map<Object, Object> hEntries(Object hash) {
        return this.getRedisTemplate().opsForHash().entries(hash);
    }

    /**
     * 批量获取值
     * @param hash
     * @param hashKeys
     * @return
     */
    public List<Object> hMultiGet(Object hash, Collection<? extends Object> hashKeys) {
        return this.getRedisTemplate().opsForHash().multiGet(hash, hashKeys);
    }

    /**
     * hash添加过个键值
     * @param hash
     * @param map
     * @return
     */
    public boolean hPutAll(Object hash, Map<? extends Object, ? extends  Object> map) {
        try {
            this.getRedisTemplate().opsForHash().putAll(hash, map);
            return true;
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * hash添加过个键值，并设置缓存时间
     * @param hash
     * @param map
     * @param seconds
     * @return
     */
    public boolean hPutAll(Object hash, Map<Object, Object> map, Long seconds) {
        try {
            this.hPutAll(hash, map);
            if (0 < seconds) {
                this.expire(hash, seconds);
            }
            return true;
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 向hash中添加元素，如果不存在将创建
     * @param hash
     * @param key
     * @param value
     * @return
     */
    public boolean hPut(Object hash, Object key, Object value) {
        try {
            this.getRedisTemplate().opsForHash().put(hash, key, value);
            return true;
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 向hash中添加元素，如果不存在将创建，并设置过期时间
     * @param hash
     * @param key
     * @param value
     * @param seconds 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return
     */
    public boolean hPut(Object hash, Object key, Object value, Long seconds) {
        try {
            this.hPut(hash, key, value);
            if (0 < seconds) {
                this.expire(hash, seconds);
            }
            return true;
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 删除hash中的值
     * @param hash
     * @param keys
     * @return
     */
    public Long hDelete(Object hash, Object... keys) {
        return this.getRedisTemplate().opsForHash().delete(hash, keys);
    }

    /**
     * 判断hash表中是否有该项值
     * @param hash
     * @param key
     * @return
     */
    public boolean hHasKey(Object hash, Object key) {
        return this.getRedisTemplate().opsForHash().hasKey(hash, key);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param hash
     * @param key
     * @return
     */
    public Long hIncrement(Object hash, Object key) {
        return this.hIncrement(hash, key, Long.valueOf(1));
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param hash
     * @param key
     * @param delta
     * @return
     */
    public Long hIncrement(Object hash, Object key, Long delta) {
        return this.getRedisTemplate().opsForHash().increment(hash, key, delta);
    }

    /**
     * hash递减
     * @param hash
     * @param key
     * @return
     */
    public Long hDecrement(Object hash, Object key) {
        return this.hDecrement(hash, key, Long.valueOf(1));
    }

    /**
     * hash递减
     * @param hash
     * @param key
     * @param delta
     * @return
     */
    public Long hDecrement(Object hash, Object key, Long delta) {
        return this.hIncrement(hash, key, -delta);
    }

    /**
     * 获取hash的元素个数
     * @param hash
     * @return
     */
    public Long hSize(Object hash) {
        return this.getRedisTemplate().opsForHash().size(hash);
    }
    /**
     * 获取set集合中的所有值
     * @param set
     * @return
     */
    public Set<Object> sMembers(Object set) {
        try {
            return this.getRedisTemplate().opsForSet().members(set);
        } catch (Throwable e) {
            this.logThrowable(e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param set
     * @param value
     * @return
     */
    public boolean sIsMember(Object set, Object value) {
        try {
            return this.getRedisTemplate().opsForSet().isMember(set, value);
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 集合添加元素
     * @param set
     * @param values
     * @return
     */
    public Long sAdd(Object set, Object... values) {
        try {
            return this.getRedisTemplate().opsForSet().add(set, values);
        } catch (Throwable e) {
            this.logThrowable(e);
            return Long.valueOf(-1);
        }
    }

    /**
     * 获取集合元素个数
     * @param set
     * @return
     */
    public Long sSize(Object set) {
        try {
            return this.getRedisTemplate().opsForSet().size(set);
        } catch (Throwable e) {
            this.logThrowable(e);
            return Long.valueOf(-1);
        }
    }

    /**
     * 删除元素
     * @param set
     * @param values
     * @return
     */
    public Long sRemove(Object set, Object... values) {
        try {
            return this.getRedisTemplate().opsForSet().remove(set, values);
        } catch (Throwable e) {
            this.logThrowable(e);
            return Long.valueOf(-1);
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param list
     * @param index
     * @param value
     * @return
     */
    public Boolean lSet(Object list, Long index, Object value) {
        try {
            this.getRedisTemplate().opsForList().set(list, index, value);
            return true;
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 获取list缓存内容
     * @param list
     * @param start
     * @param end
     * @return
     */
    public List<Object> lRange(Object list, Long start, Long end) {
        try {
            return this.getRedisTemplate().opsForList().range(list, start, end);
        } catch (Throwable e) {
            this.logThrowable(e);
            return null;
        }
    }

    /**
     * 移除N个值为value
     * @param list
     * @param start
     * @param value
     * @return
     */
    public Long lRemove(Object list, Long start, Object value) {
        try {
            return this.getRedisTemplate().opsForList().remove(list, start, value);
        } catch (Throwable e) {
            this.logThrowable(e);
            return null;
        }
    }
    /**
     * 获取list缓存长度
     * @param list
     * @return
     */
    public Long lSize(Object list) {
        try {
            return this.getRedisTemplate().opsForList().size(list);
        } catch (Throwable e) {
            this.logThrowable(e);
            return Long.valueOf(-1);
        }
    }

    /**
     * 通过索引获取list中的值
     * @param list
     * @param index
     * @return
     */
    public Object lIndex(Object list, Long index) {
        try {
            return this.getRedisTemplate().opsForList().index(list, index);
        } catch (Throwable e) {
            this.logThrowable(e);
            return null;
        }
    }

    /**
     * 入队
     * @param list
     * @param value
     * @return
     */
    public boolean lPush(Object list, Object value) {
        return this.lRightPush(list, value);
    }

    /**
     * list中添加多个数据
     * @param list
     * @param values
     * @return
     */
    public Long lPushAll(Object list, Object... values) {
        return this.lRightPushAll(list, values);
    }

    /**
     * list中添加多个数据
     * @param list
     * @param values
     * @return
     */
    public Long lPushAll(Object list, Collection<? extends Object> values) {
        return this.lRightPushAll(list, values);
    }

    /**
     * 入队并设置缓存时间
     * @param list
     * @param value
     * @param seconds
     * @return
     */
    public boolean lPush(Object list, Object value, Long seconds) {
        return this.lRightPush(list, value, seconds);
    }


    /**
     * 从右边pop一个
     * @param list
     * @return
     */
    public Object lPop(Object list) {
        return this.lLeftPop(list);
    }

    /**
     * 从左边pop一个
     * @param list
     * @return
     */
    public Object lLeftPop(Object list) {
        try {
            return this.getRedisTemplate().opsForList().leftPop(list);
        } catch (Throwable e) {
            this.logThrowable(e);
            return null;
        }
    }

    /**
     * 从右边入队
     * @param list
     * @param value
     * @return
     */
    public boolean lRightPush(Object list, Object value) {
        try {
            return this.getRedisTemplate().opsForList().rightPush(list, value) > 0 ? true : false;
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * list中添加多个数据
     * @param list
     * @param values
     * @return
     */
    public Long lRightPushAll(Object list, Object... values) {
        try {
            return this.getRedisTemplate().opsForList().rightPushAll(list, values);
        } catch (Throwable e) {
            this.logThrowable(e);
            return Long.valueOf(-1);
        }
    }

    /**
     * list中添加多个数据
     * @param list
     * @param values
     * @return
     */
    public Long lRightPushAll(Object list, Collection<? extends Object> values) {
        try {
            return this.getRedisTemplate().opsForList().rightPushAll(list, values);
        } catch (Throwable e) {
            this.logThrowable(e);
            return Long.valueOf(-1);
        }
    }

    /**
     * 从右边入队并设置缓存时间
     * @param list
     * @param value
     * @param seconds
     * @return
     */
    public boolean lRightPush(Object list, Object value, Long seconds) {
        try {
            this.lRightPush(list, value);
            if (0 < seconds) {
                this.expire(list, seconds);
            }
            return  true;
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 从左边入队
     * @param list
     * @param value
     * @return
     */
    public boolean lLeftPush(Object list, Object value) {
        try {
            return this.getRedisTemplate().opsForList().leftPush(list, value) > 0 ? true : false;
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 从左边入队并设置缓存时间
     * @param list
     * @param value
     * @param seconds
     * @return
     */
    public boolean lLeftPush(Object list, Object value, Long seconds) {
        try {
            this.lLeftPush(list, value);
            if (0 < seconds) {
                this.expire(list, seconds);
            }
            return  true;
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * list中从左边添加多个数据
     * @param list
     * @param values
     * @return
     */
    public Long lLeftPushAll(Object list, Object... values) {
        try {
            return this.getRedisTemplate().opsForList().leftPushAll(list, values);
        } catch (Throwable e) {
            this.logThrowable(e);
            return Long.valueOf(-1);
        }
    }

    /**
     * list中从左边添加多个数据
     * @param list
     * @param values
     * @return
     */
    public Long lLeftPushAll(Object list, Collection<? extends Object> values) {
        try {
            return this.getRedisTemplate().opsForList().leftPushAll(list, values);
        } catch (Throwable e) {
            this.logThrowable(e);
            return Long.valueOf(-1);
        }
    }

    /**
     * 从右边pop一个
     * @param list
     * @return
     */
    public Object lRightPop(Object list) {
        try {
            return this.getRedisTemplate().opsForList().rightPop(list);
        } catch (Throwable e) {
            this.logThrowable(e);
            return null;
        }
    }

    /**
     * 长度
     * @param key
     * @return
     */
    public Long zSize(Object key) {
        try {
            return this.getRedisTemplate().opsForZSet().size(key);
        } catch (Throwable e) {
            this.logThrowable(e);
            return Long.valueOf(0);
        }
    }

    /**
     * 有续集增加元素
     * @param key
     * @param value
     * @param score
     * @return
     */
    public Boolean zAdd(Object key, Object value, Double score) {
        try {
            return this.getRedisTemplate().opsForZSet().add(key, value, score);
        } catch (Throwable e) {
            this.logThrowable(e);
            return false;
        }
    }

    /**
     * 有序集批量添加元素
     * @param key
     * @param tuples
     * @return
     */
    public Long zAdd(Object key, Set<? extends ZSetOperations.TypedTuple> tuples) {
        try {
            return this.getRedisTemplate().opsForZSet().add(key, tuples);
        } catch (Throwable e) {
            this.logThrowable(e);
            return Long.valueOf(-1);
        }
    }

    /**
     * 分值由小到大排序取
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zRange(Object key, Long start, Long end) {
        try {
            return this.getRedisTemplate().opsForZSet().range(key, start, end);
        } catch (Throwable e) {
            this.logThrowable(e);
            return new HashSet();
        }
    }

    /**
     * 分值由大到小排序取
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zReverseRange(Object key, Long start, Long end) {
        try {
            return this.getRedisTemplate().opsForZSet().reverseRange(key, start, end);
        } catch (Throwable e) {
            this.logThrowable(e);
            return new HashSet();
        }
    }

    /**
     * 分值由小到大排序取，返回key和值的组合
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zRangeWithScores(Object key, Long start, Long end) {
        try {
            return this.getRedisTemplate().opsForZSet().rangeWithScores(key, start, end);
        } catch (Throwable e) {
            this.logThrowable(e);
            return new HashSet();
        }
    }

    /**
     * 分值由大到小排序取，返回key和值的组合
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zReverseRangeWithScores(Object key, Long start, Long end) {
        try {
            return this.getRedisTemplate().opsForZSet().reverseRangeWithScores(key, start, end);
        } catch (Throwable e) {
            this.logThrowable(e);
            return new HashSet();
        }
    }

    /**
     * 按照分值范围查找
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<Object> zReverseRangeByScore(Object key, Double min, Double max) {
        try {
            return this.getRedisTemplate().opsForZSet().reverseRangeByScore(key, min, max);
        } catch (Throwable e) {
            this.logThrowable(e);
            return new HashSet();
        }
    }
}
