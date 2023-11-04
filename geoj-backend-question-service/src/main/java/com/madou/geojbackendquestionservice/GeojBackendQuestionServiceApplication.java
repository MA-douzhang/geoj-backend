package com.madou.geojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan("com.madou.geojbackendquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.madou")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.madou.geojbackendserviceclient.service"})
public class GeojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeojBackendQuestionServiceApplication.class, args);
    }

}
