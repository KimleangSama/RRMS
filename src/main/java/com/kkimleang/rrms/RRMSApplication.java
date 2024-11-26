package com.kkimleang.rrms;


import com.redis.om.spring.annotations.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cache.annotation.*;

@EnableCaching
@SpringBootApplication
@EnableRedisEnhancedRepositories(basePackages = {"com.kkimleang.rrms.repository", "com.kkimleang.rrms.entity"})
public class RRMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(RRMSApplication.class, args);
    }
}
