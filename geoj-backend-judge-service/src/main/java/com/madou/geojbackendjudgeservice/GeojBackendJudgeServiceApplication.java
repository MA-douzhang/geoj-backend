package com.madou.geojbackendjudgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.madou")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.madou.geojbackendserviceclient.service"})
public class GeojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeojBackendJudgeServiceApplication.class, args);
    }

}
