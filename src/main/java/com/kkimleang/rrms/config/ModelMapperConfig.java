package com.kkimleang.rrms.config;

import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    public static ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        Condition<?, ?> skipNulls =
                context -> context.getSource() != null;
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setPropertyCondition(skipNulls);
        return modelMapper;
    }
}
