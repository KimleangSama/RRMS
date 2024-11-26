package com.kkimleang.rrms.service.property;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.*;
import com.kkimleang.rrms.payload.request.property.*;
import com.kkimleang.rrms.payload.response.property.*;
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
public class PropertyService {
    private final String RESOURCE = "Property";
    private final String FAILED_GET_EXCEPTION = "Failed to get property {} ";
    private final String FAILED_EDIT_EXCEPTION = "Failed to edit property {} ";
    private final PropertyRepository propertyRepository;

    @Transactional
    public PropertyResponse createProperty(CustomUserDetails user, CreatePropertyRequest request) {
        try {
            if (user == null || user.getUser() == null) {
                throw new ResourceForbiddenException("Unauthorized to create property", request);
            }
            User currentUser = user.getUser();
            if (propertyRepository.existsByUserIdAndName(currentUser.getId(), request.getName())) {
                throw new ResourceForbiddenException(RESOURCE + " with name " + request.getName() + " already exists in your assets", currentUser.getUsername());
            }
            Property property = new Property();
            PropertyMapper.createPropertyFromCreatePropertyRequest(property, request);
            property.setUser(currentUser);
            property.setCreatedBy(currentUser.getId());
            property = propertyRepository.save(property);
            return PropertyResponse.fromProperty(property);
        } catch (ResourceForbiddenException e) {
            log.error("Failed to create property {}", e.getMessage(), e);
            throw e;
        }
    }

    @Cacheable(value = "properties")
    @Transactional
    public List<PropertyResponse> getPagingProperties(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Property> properties = propertyRepository.findAll(pageable);
            if (properties.isEmpty()) {
                throw new ResourceNotFoundException(RESOURCE, "of size " + size + " at page " + page, properties);
            }
            List<Property> propertyList = properties.getContent();
            return PropertyResponse.fromProperties(propertyList);
        } catch (ResourceNotFoundException | ResourceForbiddenException e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw new ResourceException(RESOURCE, e.getMessage());
        }
    }

    @Cacheable(value = "properties", key = "#propertyId")
    @Transactional
    public PropertyResponse findPropertyById(CustomUserDetails user, UUID propertyId) {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, "Id", propertyId));
            if (user == null || user.getUser() == null) {
                return PropertyResponse.fromProperty(property);
            }
            return PropertyResponse.fromProperty(user.getUser(), property);
        } catch (ResourceNotFoundException | ResourceForbiddenException e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw new ResourceException(RESOURCE, e.getMessage());
        }
    }

    @Cacheable(value = "properties", key = "#landlordId")
    @Transactional
    public List<PropertyOverviewResponse> getLandlordProperties(CustomUserDetails user, UUID landlordId) {
        try {
            List<Property> properties = propertyRepository.findByUserId(landlordId);
            if (properties.isEmpty()) {
                throw new ResourceNotFoundException(RESOURCE, "of landlord " + landlordId, properties);
            }
            if (user == null || user.getUser() == null) {
                return PropertyOverviewResponse.fromProperties(null, properties);
            }
            return PropertyOverviewResponse.fromProperties(user.getUser(), properties);
        } catch (ResourceNotFoundException | ResourceEditionException e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw new RuntimeException(FAILED_GET_EXCEPTION, e);
        }
    }

    @CachePut(value = "properties", key = "#propertyId")
    @Transactional
    public PropertyResponse editPropertyContact(CustomUserDetails user, UUID propertyId, EditPropertyContactRequest request) {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, "Id", propertyId));
            if (withPrivilege(user, property)) {
                throw new ResourceForbiddenException("Unauthorized to delete property", property);
            }
            PropertyMapper.updatePropertyContactFromEditPropertyContactRequest(property, request);
            property.setUpdatedBy(user.getUser().getId());
            property.setUpdatedAt(Instant.now());
            property = propertyRepository.save(property);
            return PropertyResponse.fromProperty(property);
        } catch (ResourceForbiddenException | ResourceNotFoundException e) {
            log.error(FAILED_EDIT_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_EDIT_EXCEPTION, e.getMessage(), e);
            throw new ResourceEditionException(FAILED_EDIT_EXCEPTION + "info " + e.getMessage());
        }
    }

    @Transactional
    public PropertyResponse editPropertyInfo(CustomUserDetails user, UUID propertyId, EditPropertyBasicRequest request) {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, "Id", propertyId));
            if (withPrivilege(user, property)) {
                throw new ResourceForbiddenException("Unauthorized to delete property", property);
            }
            PropertyMapper.updatePropertyInfoFromEditPropertyInfoRequest(property, request);
            property.setUpdatedBy(user.getUser().getId());
            property.setUpdatedAt(Instant.now());
            property = propertyRepository.save(property);
            return PropertyResponse.fromProperty(property);
        } catch (ResourceForbiddenException | ResourceNotFoundException e) {
            log.error(FAILED_EDIT_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (ResourceEditionException e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_EDIT_EXCEPTION, e.getMessage(), e);
            throw new ResourceEditionException(FAILED_EDIT_EXCEPTION + "info " + e.getMessage());
        }
    }

    public PropertyResponse deleteProperty(CustomUserDetails user, UUID propertyId) {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, "Id", propertyId));
            PropertyResponse propertyResponse = PropertyResponse.fromProperty(property);
            if (withPrivilege(user, property)) {
                throw new ResourceForbiddenException("Unauthorized to delete property", property);
            }
            property.setDeletedBy(user.getUser().getId());
            property.setDeletedAt(Instant.now());
            propertyRepository.save(property);
            return propertyResponse;
        } catch (ResourceForbiddenException | ResourceNotFoundException | ResourceDeletedException e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_EDIT_EXCEPTION, e.getMessage(), e);
            throw new ResourceEditionException(FAILED_EDIT_EXCEPTION + e.getMessage());
        }
    }

    private boolean withPrivilege(CustomUserDetails user, Property property) {
        return user == null || user.getUser() == null || !property.getUser().getId().equals(user.getUser().getId());
    }

    public List<PropertyResponse> getRecommendedProperties(CustomUserDetails user) {
        try {
            User currentUser = user.getUser();
            List<Property> properties = propertyRepository.findNearbyProperties(
                    currentUser.getPreferredLatitude(),
                    currentUser.getPreferredLongitude(),
                    currentUser.getPreferredRadius()
            );
            return PropertyResponse.fromProperties(currentUser, properties);
        } catch (ResourceNotFoundException | ResourceEditionException e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(FAILED_GET_EXCEPTION, e.getMessage(), e);
            throw new RuntimeException("Failed to get all properties" + e.getMessage());
        }
    }
}
