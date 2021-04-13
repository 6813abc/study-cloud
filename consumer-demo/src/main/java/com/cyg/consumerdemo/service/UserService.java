package com.cyg.consumerdemo.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

/**
 * @author cyg
 * @date 2021/4/12 17:10
 **/
@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @CacheResult(cacheKeyMethod = "getCacheKey")
    @HystrixCommand(fallbackMethod = "getDefaultUser", commandKey = "getUserCache")
    public String getUserCache(int id) {
        System.out.println("getUserCache id:" + id);
        return this.restTemplate.getForObject("http://producer/provider/demo?index={1}", String.class, id);
    }

    @CacheRemove(commandKey = "getUserCache", cacheKeyMethod = "getCacheKey")
    @HystrixCommand
    public String removeCache(int id) {
        System.out.println("removeCache id:" + id);
        return this.restTemplate.getForObject("http://producer/provider/demo?index={1}", String.class, id);
    }

    /**
     * 为缓存生成key的方法
     */
    public String getCacheKey(int id) {
        return String.valueOf(id);
    }

    public String getDefaultUser(@PathVariable int id) {
        return "默认用户";
    }
}
