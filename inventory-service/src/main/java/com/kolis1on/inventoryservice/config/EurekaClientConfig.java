package com.kolis1on.inventoryservice.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class EurekaClientConfig {



    @Bean
    public EurekaClientConfigBean eurekaClientConfigBean() {
        EurekaClientConfigBean config = new EurekaClientConfigBean();

        // Set service URL properties
        Map<String, String> serviceUrl = new HashMap<>();
        serviceUrl.put("defaultZone", String.format("http://%s:%s@localhost:8761/eureka", "eureka", "password"));
        config.setServiceUrl(serviceUrl);

        return config;
    }
}