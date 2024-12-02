package com.kkimleang.rrms;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.property.*;
import com.kkimleang.rrms.repository.property.*;
import com.kkimleang.rrms.repository.user.*;
import java.util.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

@Slf4j
@SpringBootTest
class PropertyServiceTests {
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createPropertyTest() {
        Property property = new Property();
        property.setName("string");
        User user = userRepository.findByEmail("super.admin@rrms.com").orElse(null);
        Assertions.assertNotNull(user);
        property.setUser(user);
        property.setEmail("string");
        property.setContact("string");
        property.setWebsite("string");
        property.setDescription("string");
        property.setPictureCover("string123");
        property.setAddressProof("string");
        property.setVillage("string");
        property.setDistrict("string");
        property.setProvince("string");
        property.setZipCode("string");
        property.setAddressGMap("string");
        property.setLatitude(0.1);
        property.setLongitude(0.1);
        property.setPropertyStatus(PropertyStatus.PENDING);
        property.setPropertyType(PropertyType.HOUSE);
        property.setPropRoomPictures(new HashSet<>());
        property = propertyRepository.save(property);
        log.info("Property: {}", property);
        Assertions.assertNotNull(property.getId());
    }
}