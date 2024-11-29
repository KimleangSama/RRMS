package com.kkimleang.rrms;


import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
@EnableRedisEnhancedRepositories(basePackages = {"com.kkimleang.rrms.repository", "com.kkimleang.rrms.entity"})
public class RRMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(RRMSApplication.class, args);
    }
}
