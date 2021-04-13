package com.cyg.consumerdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

//Hystrix断路器监控
@EnableHystrixDashboard
//开启Hystrix的断路器功能
@EnableCircuitBreaker
@SpringBootApplication
public class ConsumerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerDemoApplication.class, args);
    }

}
