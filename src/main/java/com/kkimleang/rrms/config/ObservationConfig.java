package com.kkimleang.rrms.config;

import io.micrometer.observation.*;
import io.micrometer.observation.aop.*;
import org.springframework.context.annotation.*;

@Configuration
public class ObservationConfig {
    @Bean
    public ObservedAspect observedAspect(ObservationRegistry registry) {
        return new ObservedAspect(registry);
    }
}
