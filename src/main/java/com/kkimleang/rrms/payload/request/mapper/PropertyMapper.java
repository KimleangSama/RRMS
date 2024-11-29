package com.kkimleang.rrms.payload.request.mapper;

import com.kkimleang.rrms.config.ModelMapperConfig;
import com.kkimleang.rrms.entity.Property;
import com.kkimleang.rrms.enums.property.PropertyStatus;
import com.kkimleang.rrms.enums.property.PropertyType;
import com.kkimleang.rrms.payload.request.property.CreatePropertyRequest;
import com.kkimleang.rrms.payload.request.property.EditPropertyBasicRequest;
import com.kkimleang.rrms.payload.request.property.EditPropertyContactRequest;
import org.modelmapper.ModelMapper;

public class PropertyMapper {
    private static final ModelMapper modelMapper = ModelMapperConfig.modelMapper();

    public static void createPropertyFromCreatePropertyRequest(Property property, CreatePropertyRequest request) {
        if (request == null) return;
        try {
            mapEnumFields(property, request.getStatus(), request.getType());
            modelMapper.map(request, property);
        } catch (Exception e) {
            setDefaultPropertyValues(property);
        }
    }

    public static void editPropertyContactFromEditPropertyContactRequest(Property property, EditPropertyContactRequest request) {
        if (request == null) return;
        try {
            modelMapper.map(request, property);
        } catch (Exception e) {
            property.setContact(null);
        }
    }

    public static void editPropertyInfoFromEditPropertyInfoRequest(Property property, EditPropertyBasicRequest request) {
        if (request == null) return;
        try {
            mapEnumFields(property, request.getPropertyStatus(), request.getPropertyType());
            modelMapper.map(request, property);
        } catch (Exception e) {
            // Keep existing values
        }
    }

    private static void mapEnumFields(Property property, String status, String type) {
        if (status != null) {
            property.setPropertyStatus(PropertyStatus.valueOf(status));
        }
        if (type != null) {
            property.setPropertyType(PropertyType.valueOf(type));
        }
    }

    private static void setDefaultPropertyValues(Property property) {
        property.setPropertyStatus(PropertyStatus.PENDING);
        property.setPropertyType(PropertyType.HOUSE);
    }
}