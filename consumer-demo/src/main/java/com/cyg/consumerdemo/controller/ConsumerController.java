package com.cyg.consumerdemo.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import com.sun.jmx.snmp.SnmpInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author cyg
 * @date 2021/1/22 10:35
 **/
@RestController
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    int anInt = 0;

    @HystrixCommand(fallbackMethod = "defaultForDemo")
    @RequestMapping("/demo")
    public String consumerDemo() {
        anInt++;
        System.out.println("anInt:" + anInt);
        return this.restTemplate.getForObject("http://producer/provider/demo?index={1}", String.class, anInt);
    }

    @RequestMapping("/getInterfaceInfo")
    public String getInterfaceInfo() {
        ServiceInstance choose = loadBalancerClient.choose("producer");
        System.out.println(choose.getPort());
        return choose.getPort() + "";
    }

    /**
     * 提供给consumerDemo方法的默认返回
     **/
    public String defaultForDemo() {
        return "测试断路器，默认返回";
    }

    /**
     * 为缓存生成key的方法
     */
    public String getCacheKey(int value) {
        return String.valueOf(value);
    }

    @RequestMapping("/order")
    public String order() {
        return this.restTemplate.getForObject("http://producer/provider/order?", String.class);
    }

    @RequestMapping("/setOrderNum")
    public String order(int num) {
        return this.restTemplate.getForObject("http://producer/provider/setOrderNum?num={1}", String.class, num);
    }
}

