package com.cyg.consumerdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
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

    @RequestMapping("/consumer/demo")
    public String consumerDemo() {
        //return this.restTemplate.getForObject("http://localhost:7900/provider/demo", String.class);
        return this.restTemplate.getForObject("http://provider-demo:7900/provider/demo", String.class);
    }

    @RequestMapping("/consumer/getInterfaceInfo")
    public String getInterfaceInfo() {
        ServiceInstance choose = loadBalancerClient.choose("provider-demo");
        System.out.println(choose.getPort());
        return choose.getPort() + "";
    }
}

