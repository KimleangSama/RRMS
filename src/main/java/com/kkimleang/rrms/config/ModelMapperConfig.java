package com.kkimleang.rrms.config;

import org.modelmapper.Condition;
import org.modelmapper.*;
import org.modelmapper.convention.*;
import org.springframework.context.annotation.*;

public class ModelMapperConfig {
    private ModelMapperConfig() {
        throw new IllegalStateException("Utility class");
    }

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
