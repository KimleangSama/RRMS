package com.kkimleang.rrms.service.property;

import static com.kkimleang.rrms.constant.PropertyLogErrorMessage.*;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.*;
import com.kkimleang.rrms.payload.request.property.*;
import com.kkimleang.rrms.payload.response.property.*;
import com.kkimleang.rrms.repository.file.*;
import com.kkimleang.rrms.repository.property.*;
import com.kkimleang.rrms.service.user.*;
import com.kkimleang.rrms.util.*;
import jakarta.transaction.*;

import java.time.*;
import java.util.*;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"properties"})
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final PropRoomPictureRepository propRoomPictureRepository;

    private void validateUserPrivilege(CustomUserDetails user, Property property) {
        if (PrivilegeChecker.isCreator(user.getUser(), property.getCreatedBy()) ||
                PrivilegeChecker.isPropertyOwner(user.getUser(), property)) {
            return;
        }
        throw new ResourceForbiddenException("Unauthorized to access property", property);
    }

    private Property findPropertyById(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException(PROPERTY, "id: " + propertyId));
        DeletableEntityValidator.validate(property, PROPERTY);
        return property;
    }

    @CacheEvict(value = "properties", allEntries = true)
    @Transactional
    public PropertyResponse createProperty(CustomUserDetails user, CreatePropertyRequest request) {
        DeletableEntityValidator.validateUser(user);
        User currentUser = user.getUser();
        checkPropertyDuplication(currentUser.getId(), request.getName());
        Property property = buildNewProperty(currentUser, request);
        Property savedProperty = propertyRepository.save(property);
        updatePicturesWithProperty(savedProperty, property.getPropRoomPictures());
        return PropertyResponse.fromProperty(currentUser, savedProperty);
    }

    private void checkPropertyDuplication(UUID userId, String propertyName) {
        if (propertyRepository.existsByUserIdAndName(userId, propertyName)) {
            throw new ResourceDuplicationException(PROPERTY_ALREADY_EXISTS,
                    String.format("%s and %s", userId, propertyName));
        }
    }

    private Property buildNewProperty(User currentUser, CreatePropertyRequest request) {
        Set<PropRoomPicture> pictures = propRoomPictureRepository.findByIdIn(request.getPictures());
        Property property = new Property();
        PropertyMapper.createPropertyFromCreatePropertyRequest(property, request);
        property.setUser(currentUser);
        property.setCreatedBy(currentUser.getId());
        property.setPropRoomPictures(pictures);
        return property;
    }

    private void updatePicturesWithProperty(Property property, Set<PropRoomPicture> pictures) {
        pictures.forEach(picture -> picture.setProperty(property));
        propRoomPictureRepository.saveAll(pictures);
    }

    @Cacheable(value = "properties")
    @Transactional
    public List<PropertyResponse> getPagingProperties(CustomUserDetails user, int page, int size) {
        try {
            Page<Property> properties = propertyRepository.findAll(PageRequest.of(page, size));
            if (properties.isEmpty()) {
                throw new ResourceNotFoundException(PROPERTY, String.format("of size %d at page %d", size, page));
            }
            return createPropertyResponse(user, properties.getContent());
        } catch (ResourceNotFoundException e) {
            log.error("Failed to get properties: {}", e.getMessage(), e);
            throw e;
        }
    }

    private List<PropertyResponse> createPropertyResponse(CustomUserDetails user, List<Property> properties) {
        return Optional.ofNullable(user)
                .map(CustomUserDetails::getUser)
                .map(currentUser -> PropertyResponse.fromProperties(currentUser, properties))
                .orElseGet(() -> PropertyResponse.fromProperties(null, properties));
    }

    @Cacheable(key = "#propertyId")
    @Transactional
    public PropertyResponse findPropertyById(CustomUserDetails user, UUID propertyId) {
        Property property = findPropertyById(propertyId);
        DeletableEntityValidator.validate(property, PROPERTY);
        return createSinglePropertyResponse(user, property);
    }

    private PropertyResponse createSinglePropertyResponse(CustomUserDetails user, Property property) {
        return Optional.ofNullable(user)
                .map(CustomUserDetails::getUser)
                .map(currentUser -> PropertyResponse.fromProperty(currentUser, property))
                .orElseGet(() -> PropertyResponse.fromProperty(null, property));
    }

    @Cacheable(key = "#landlordId")
    @Transactional
    public List<PropertyOverviewResponse> getLandlordProperties(CustomUserDetails user, UUID landlordId) {
        List<Property> properties = propertyRepository.findByUserId(landlordId);
        if (properties.isEmpty()) {
            throw new ResourceNotFoundException(PROPERTY, "of landlord id " + landlordId);
        }
        return createPropertyOverviewResponse(user, properties);
    }

    private List<PropertyOverviewResponse> createPropertyOverviewResponse(CustomUserDetails user, List<Property> properties) {
        return Optional.ofNullable(user)
                .map(CustomUserDetails::getUser)
                .map(currentUser -> PropertyOverviewResponse.fromProperties(currentUser, properties))
                .orElseGet(() -> PropertyOverviewResponse.fromProperties(null, properties));
    }

    @CachePut(key = "#propertyId")
    @Transactional
    public PropertyResponse editPropertyContact(CustomUserDetails user, UUID propertyId, EditPropertyContactRequest request) {
        Property property = findPropertyById(propertyId);
        validateUserPrivilege(user, property);
        return editProperty(user, property, p -> PropertyMapper.editPropertyContactFromEditPropertyContactRequest(p, request));
    }

    @CachePut(key = "#propertyId")
    @Transactional
    public PropertyResponse editPropertyInfo(CustomUserDetails user, UUID propertyId, EditPropertyBasicRequest request) {
        Property property = findPropertyById(propertyId);
        validateUserPrivilege(user, property);
        return editProperty(user, property, p -> PropertyMapper.editPropertyInfoFromEditPropertyInfoRequest(p, request));
    }

    private PropertyResponse editProperty(CustomUserDetails user, Property property, java.util.function.Consumer<Property> updateFunction) {
        updateFunction.accept(property);
        property.setUpdatedBy(user.getUser().getId());
        property.setUpdatedAt(Instant.now());
        return PropertyResponse.fromProperty(user.getUser(), propertyRepository.save(property));
    }

    @CacheEvict(key = "#propertyId")
    @Transactional
    public PropertyResponse deleteProperty(CustomUserDetails user, UUID propertyId) {
        Property property = findPropertyById(propertyId);
        validateUserPrivilege(user, property);
        PropertyResponse response = PropertyResponse.fromProperty(user.getUser(), property);
        property.setDeletedBy(user.getUser().getId());
        property.setDeletedAt(Instant.now());
        property.setName(RandomString.make(16));
        propertyRepository.save(property);
        return response;
    }

    @Cacheable(value = "recommended-properties", key = "#user.user.id")
    public List<PropertyResponse> getRecommendedProperties(CustomUserDetails user) {
        DeletableEntityValidator.validateUser(user);
        User currentUser = user.getUser();
        List<Property> properties = propertyRepository.findNearbyProperties(
                currentUser.getPreferredLatitude(),
                currentUser.getPreferredLongitude(),
                currentUser.getPreferredRadius()
        );
        return PropertyResponse.fromProperties(currentUser, properties);
    }
}