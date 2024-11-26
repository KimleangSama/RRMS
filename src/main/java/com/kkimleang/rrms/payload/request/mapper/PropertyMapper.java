package com.kkimleang.rrms.payload.request.mapper;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.property.*;
import com.kkimleang.rrms.payload.request.property.*;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.Optional;

public class PropertyMapper {
    public static void createPropertyFromCreatePropertyRequest(Property property, CreatePropertyRequest request) {
        try {
            if (request == null) {
                return;
            }
            Optional.ofNullable(request.getStatus()).ifPresent(status -> property.setPropertyStatus(PropertyStatus.valueOf(status)));
            Optional.ofNullable(request.getType()).ifPresent(type -> property.setPropertyType(PropertyType.valueOf(type)));
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.map(request, property);
        } catch (Exception e) {
            property.setPropertyStatus(PropertyStatus.PENDING);
            property.setPropertyType(PropertyType.HOUSE);
        }
    }

    public static void updatePropertyContactFromEditPropertyContactRequest(Property property, EditPropertyContactRequest request) {
        try {
            if (request == null) {
                return;
            }
            ModelMapper modelMapper = new ModelMapper();
            Condition<?, ?> skipNulls =
                    context -> context.getSource() != null;
            modelMapper.getConfiguration()
                    .setMatchingStrategy(MatchingStrategies.STRICT)
                    .setPropertyCondition(skipNulls);
            modelMapper.map(request, property);
        } catch (Exception e) {
            property.setContact(null);
        }
    }

    public static void updatePropertyInfoFromEditPropertyInfoRequest(Property property, EditPropertyBasicRequest request) {
        try {
            if (request == null) {
                return;
            }
            Optional.ofNullable(request.getPropertyStatus()).ifPresent(status -> property.setPropertyStatus(PropertyStatus.valueOf(status)));
            Optional.ofNullable(request.getPropertyType()).ifPresent(type -> property.setPropertyType(PropertyType.valueOf(type)));
            ModelMapper modelMapper = new ModelMapper();
            Condition<?, ?> skipNulls =
                    context -> context.getSource() != null;
            modelMapper.getConfiguration()
                    .setMatchingStrategy(MatchingStrategies.STRICT)
                    .setPropertyCondition(skipNulls);
            modelMapper.map(request, property);
        } catch (Exception e) {
            property.setPropertyStatus(property.getPropertyStatus());
            property.setPropertyType(property.getPropertyType());
        }
    }
}
