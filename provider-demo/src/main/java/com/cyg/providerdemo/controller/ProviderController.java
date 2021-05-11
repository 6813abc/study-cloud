package com.cyg.providerdemo.controller;

import com.cyg.providerdemo.util.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cyg
 * @date 2021/1/22 10:26
 **/
@RestController
@Slf4j
public class ProviderController {

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;
    final JedisPool jedisPool;

    public ProviderController(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @RequestMapping("/provider/demo")
    public String providerDemo(int index) {
        System.out.println("收到消息" + index);
        return "收到消息";
    }

    @RequestMapping("/provider/order")
    public String order() throws InterruptedException {
        Jedis jedis = jedisPool.getResource();
        Random random = new Random(5);
        SnowFlake snowFlake;
        synchronized (this) {
            int workerId = random.nextInt(16);
            int dataCenterId = random.nextInt(16);
            System.out.println(workerId + ":" + dataCenterId);
            snowFlake = new SnowFlake(workerId, dataCenterId);
        }
        long requestId = snowFlake.nextId();
        log.info(requestId + "");
        String key = "order";
        //加锁
        int productNum = 0;
        try {
            if (lock(jedis, key, Long.toString(requestId))) {
                //查库存
                productNum = Integer.parseInt(jedis.get("orderNum"));
                if (productNum == 0) {
                    log.info("卖光" + requestId);
                    return "卖光";
                }
                //减库存
                productNum--;
                //写库存
                setOrderNum(Integer.toString(productNum));
                log.info(requestId + "加锁成功,剩余库存：" + productNum);
            } else {
                log.error(requestId + "下单失败");
                return "下单失败";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //解锁
            if (unlock(jedis, key, Long.toString(requestId))) {
                log.info(requestId + "解锁成功");
            }
            jedis.close();
        }
        return "下单成功，当前库存:" + productNum;
    }

    @RequestMapping("/provider/setOrderNum")
    public String setOrderNum(String num) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("orderNum", num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "更新库存成功";
    }

    /**
     * @param key   锁的名称
     * @param value 给当前请求唯一标识，谁加锁必须谁解锁
     **/
    private boolean lock(Jedis jedis, String key, String value) {
        String result = jedis.set(key, value, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, 2000);
        return LOCK_SUCCESS.equals(result);
    }

    /**
     * @param key   锁的名称
     * @param value 给当前请求唯一标识，谁加锁必须谁解锁
     **/
    private boolean unlock(Jedis jedis, String key, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(value));
        return RELEASE_SUCCESS.equals(result);
    }
}
