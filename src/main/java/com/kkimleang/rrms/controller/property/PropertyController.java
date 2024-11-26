package com.kkimleang.rrms.controller.property;

import com.kkimleang.rrms.annotation.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.*;
import com.kkimleang.rrms.payload.request.property.*;
import com.kkimleang.rrms.payload.response.property.*;
import com.kkimleang.rrms.service.property.*;
import com.kkimleang.rrms.service.user.*;
import jakarta.transaction.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.dao.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/property")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyService propertyService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @PostMapping("/create")
    public Response<PropertyResponse> createProperty(@CurrentUser CustomUserDetails user, @RequestBody CreatePropertyRequest request) {
        try {
            PropertyResponse propertyResponse = propertyService.createProperty(user, request);
            return Response.<PropertyResponse>created()
                    .setPayload(propertyResponse);
        } catch (ResourceForbiddenException e) {
            return Response.<PropertyResponse>badRequest()
                    .setErrors(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create property {}", e.getMessage(), e);
            return Response.<PropertyResponse>badRequest()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to create property {}", e.getMessage(), e);
            return Response.<PropertyResponse>exception()
                    .setErrors(e.getMessage());
        }
    }

    @GetMapping("/all")
    public Response<List<PropertyResponse>> getAllProperties(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            List<PropertyResponse> propertyResponses = propertyService.getPagingProperties(page, size);
            return Response.<List<PropertyResponse>>ok()
                    .setPayload(propertyResponses);
        } catch (ResourceNotFoundException e) {
            return Response.<List<PropertyResponse>>notFound()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            return Response.<List<PropertyResponse>>exception()
                    .setErrors(e.getMessage());
        }
    }

    @GetMapping("/{id}/overview")
    public Response<PropertyOverviewResponse> getPropertyOverView(@CurrentUser CustomUserDetails user, @PathVariable("id") UUID propertyId) {
        try {
            PropertyResponse propertyResponse = propertyService.findPropertyById(user, propertyId);
            PropertyOverviewResponse property = PropertyOverviewResponse.fromPropertyResponse(propertyResponse);
            return Response.<PropertyOverviewResponse>ok()
                    .setPayload(property);
        } catch (ResourceNotFoundException | ResourceForbiddenException e) {
            return Response.<PropertyOverviewResponse>notFound().setErrors(e.getMessage());
        } catch (Exception e) {
            return Response.<PropertyOverviewResponse>exception().setErrors(e.getMessage());
        }
    }

    @GetMapping("/{id}/view")
    public Response<PropertyResponse> getPropertyView(@CurrentUser CustomUserDetails user, @PathVariable("id") UUID propertyId) {
        try {
            PropertyResponse property = propertyService.findPropertyById(user, propertyId);
            return Response.<PropertyResponse>ok()
                    .setPayload(property);
        } catch (ResourceNotFoundException | ResourceForbiddenException e) {
            return Response.<PropertyResponse>notFound().setErrors(e.getMessage());
        } catch (Exception e) {
            return Response.<PropertyResponse>exception().setErrors(e.getMessage());
        }
    }

    @GetMapping("/landlord/all")
    public Response<List<PropertyOverviewResponse>> getAllLandlordProperties(
            @CurrentUser CustomUserDetails user,
            @RequestParam("landlordId") UUID landlordId
    ) {
        try {
            List<PropertyOverviewResponse> propertyResponses = propertyService.getLandlordProperties(user, landlordId);
            return Response.<List<PropertyOverviewResponse>>ok()
                    .setPayload(propertyResponses);
        } catch (ResourceNotFoundException e) {
            return Response.<List<PropertyOverviewResponse>>notFound()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            return Response.<List<PropertyOverviewResponse>>exception()
                    .setErrors(e.getMessage());
        }
    }

    @PatchMapping("/{id}/edit-contact")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Response<PropertyResponse> editProperty(
            @CurrentUser CustomUserDetails user,
            @PathVariable("id") UUID propertyId,
            @RequestBody EditPropertyContactRequest request
    ) {
        try {
            PropertyResponse propertyResponse = propertyService.editPropertyContact(user, propertyId, request);
            return Response.<PropertyResponse>ok()
                    .setPayload(propertyResponse);
        } catch (ResourceNotFoundException e) {
            return Response.<PropertyResponse>notFound()
                    .setErrors(e.getMessage());
        } catch (ResourceForbiddenException e) {
            return Response.<PropertyResponse>badRequest()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            return Response.<PropertyResponse>exception()
                    .setErrors(e.getMessage());
        }
    }

    @PatchMapping("/{id}/edit-info")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Response<PropertyResponse> editPropertyInfo(
            @CurrentUser CustomUserDetails user,
            @PathVariable("id") UUID propertyId,
            @RequestBody EditPropertyBasicRequest request
    ) {
        try {
            PropertyResponse propertyResponse = propertyService.editPropertyInfo(user, propertyId, request);
            return Response.<PropertyResponse>ok()
                    .setPayload(propertyResponse);
        } catch (ResourceNotFoundException e) {
            return Response.<PropertyResponse>notFound()
                    .setErrors(e.getMessage());
        } catch (ResourceForbiddenException e) {
            return Response.<PropertyResponse>badRequest()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            return Response.<PropertyResponse>exception()
                    .setErrors(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('LANDLORD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public Response<PropertyResponse> deleteProperty(@CurrentUser CustomUserDetails user, @PathVariable("id") UUID propertyId) {
        try {
            PropertyResponse propertyResponse = propertyService.deleteProperty(user, propertyId);
            return Response.<PropertyResponse>ok()
                    .setPayload(propertyResponse);
        } catch (ResourceNotFoundException | ResourceDeletedException e) {
            return Response.<PropertyResponse>notFound()
                    .setErrors(e.getMessage());
        } catch (ResourceForbiddenException e) {
            return Response.<PropertyResponse>accessDenied()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            return Response.<PropertyResponse>exception()
                    .setErrors(e.getMessage());
        }
    }

    @Transactional
    @GetMapping("/recommended")
    public Response<List<PropertyResponse>> getRecommendedProperties(
            @CurrentUser CustomUserDetails user
    ) {
        try {
            if (user == null || user.getUser() == null) {
                throw new ResourceForbiddenException("You must login and set preferred location to get recommended properties.", "properties");
            }
            List<PropertyResponse> propertyResponses = propertyService.getRecommendedProperties(user);
            return Response.<List<PropertyResponse>>ok()
                    .setPayload(propertyResponses);
        } catch (ResourceNotFoundException e) {
            return Response.<List<PropertyResponse>>notFound()
                    .setErrors(e.getMessage());
        } catch (Exception e) {
            return Response.<List<PropertyResponse>>exception()
                    .setErrors(e.getMessage());
        }
    }
}
