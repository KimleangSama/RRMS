package com.kkimleang.rrms.service.property;

import static com.kkimleang.rrms.constant.PrivilegeLogErrorMessage.*;
import static com.kkimleang.rrms.constant.PropertyLogErrorMessage.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.*;
import com.kkimleang.rrms.payload.request.property.*;
import com.kkimleang.rrms.payload.response.property.*;
import com.kkimleang.rrms.repository.file.*;
import com.kkimleang.rrms.repository.property.*;
import com.kkimleang.rrms.service.user.*;
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

    private void validateUser(CustomUserDetails user) {
        Optional.ofNullable(user)
                .map(CustomUserDetails::getUser)
                .orElseThrow(() -> new ResourceForbiddenException(FORBIDDEN, user));
    }

    private void validateUserPrivilege(CustomUserDetails user, Property property) {
        try {
            validateUser(user);
            if (!Objects.equals(property.getUser().getId(), user.getUser().getId())) {
                throw new ResourceForbiddenException(FORBIDDEN, property);
            }
        } catch (ResourceForbiddenException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private Property findPropertyById(UUID propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException(PROPERTY, "Id"));
    }

    @Transactional
    public PropertyResponse createProperty(CustomUserDetails user, CreatePropertyRequest request) {
        validateUser(user);
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

    @Cacheable
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
                .orElseGet(() -> PropertyResponse.fromProperties(properties));
    }

    @Cacheable(key = "#propertyId")
    @Transactional
    public PropertyResponse findPropertyById(CustomUserDetails user, UUID propertyId) {
        try {
            Property property = findPropertyById(propertyId);
            return createSinglePropertyResponse(user, property);
        } catch (ResourceDeletionException e) {
            log.error("{}", e.getMessage(), e);
            throw e;
        } catch (ResourceNotFoundException e) {
            log.error("Failed to find property: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to find property: {}", e.getMessage(), e);
            throw new ResourceException(PROPERTY_GET_FAILED, e.getMessage());
        }
    }

    private PropertyResponse createSinglePropertyResponse(CustomUserDetails user, Property property) {
        return Optional.ofNullable(user)
                .map(CustomUserDetails::getUser)
                .map(currentUser -> PropertyResponse.fromProperty(currentUser, property))
                .orElseGet(() -> PropertyResponse.fromProperty(property));
    }

    @Cacheable(key = "#landlordId")
    @Transactional
    public List<PropertyOverviewResponse> getLandlordProperties(CustomUserDetails user, UUID landlordId) {
        try {
            List<Property> properties = propertyRepository.findByUserId(landlordId);
            if (properties.isEmpty()) {
                throw new ResourceNotFoundException(PROPERTY, "of landlord id " + landlordId);
            }
            return createPropertyOverviewResponse(user, properties);
        } catch (Exception e) {
            log.error("Failed to get landlord properties: {}", e.getMessage(), e);
            throw new ResourceException(PROPERTY_GET_FAILED, e.getMessage());
        }
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
        return PropertyResponse.fromProperty(propertyRepository.save(property));
    }

    @CacheEvict(key = "#propertyId")
    @Transactional
    public PropertyResponse deleteProperty(CustomUserDetails user, UUID propertyId) {
        try {
            Property property = findPropertyById(propertyId);
            validateUserPrivilege(user, property);
            PropertyResponse response = PropertyResponse.fromProperty(property);
            property.setDeletedBy(user.getUser().getId());
            property.setDeletedAt(Instant.now());
            propertyRepository.save(property);
            return response;
        } catch (Exception e) {
            log.error("Failed to delete property: {}", e.getMessage(), e);
            throw new ResourceDeletionException(PROPERTY, e.getMessage());
        }
    }

    @Cacheable(value = "recommended-properties", key = "#user.user.id")
    public List<PropertyResponse> getRecommendedProperties(CustomUserDetails user) {
        validateUser(user);
        try {
            User currentUser = user.getUser();
            List<Property> properties = propertyRepository.findNearbyProperties(
                    currentUser.getPreferredLatitude(),
                    currentUser.getPreferredLongitude(),
                    currentUser.getPreferredRadius()
            );
            return PropertyResponse.fromProperties(currentUser, properties);
        } catch (Exception e) {
            log.error("Failed to get recommended properties: {}", e.getMessage(), e);
            throw new ResourceException(PROPERTY_GET_FAILED, e.getMessage());
        }
    }
}