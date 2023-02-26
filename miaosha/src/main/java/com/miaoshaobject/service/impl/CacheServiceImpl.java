package com.miaoshaobject.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.miaoshaobject.service.CacheService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyi
 * @create 2022-11-05-9:37
 */
@Service
public class CacheServiceImpl implements CacheService {
    private Cache<String,Object> commonCache = null;

    @PostConstruct
    public void init(){
        commonCache = CacheBuilder.newBuilder()
                //设置缓存的初始容量为10
                .initialCapacity(10)
                //设置缓存中最大存储100key，超过100个之后就会按照LRU的策略移除缓存项
                .maximumSize(100)
                //设置写缓存后多少秒过期
                .expireAfterWrite(60,TimeUnit.SECONDS).build();
    }
    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key,value);
    }

    @Override
    public Object getFromCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
