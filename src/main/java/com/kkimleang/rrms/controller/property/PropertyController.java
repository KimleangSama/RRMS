package com.kkimleang.rrms.controller.property;

import com.kkimleang.rrms.annotation.CurrentUser;
import com.kkimleang.rrms.controller.GlobalControllerServiceCall;
import com.kkimleang.rrms.payload.Response;
import com.kkimleang.rrms.payload.request.property.CreatePropertyRequest;
import com.kkimleang.rrms.payload.request.property.EditPropertyBasicRequest;
import com.kkimleang.rrms.payload.request.property.EditPropertyContactRequest;
import com.kkimleang.rrms.payload.response.property.PropertyOverviewResponse;
import com.kkimleang.rrms.payload.response.property.PropertyResponse;
import com.kkimleang.rrms.service.property.PropertyService;
import com.kkimleang.rrms.service.user.CustomUserDetails;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/property")
@RequiredArgsConstructor
public class PropertyController {
    private final GlobalControllerServiceCall service;
    private final PropertyService propertyService;

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PostMapping("/create")
    public Response<PropertyResponse> createProperty(
            @CurrentUser CustomUserDetails user,
            @Valid @RequestBody CreatePropertyRequest request) {
        return service.executeServiceCall(() -> propertyService.createProperty(user, request),
                "Failed to create property");
    }

    @GetMapping("/all")
    public Response<List<PropertyResponse>> getAllProperties(
            @CurrentUser CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.executeServiceCall(() -> propertyService.getPagingProperties(user, page, size),
                "Failed to fetch properties");
    }

    @GetMapping("/{id}/overview")
    public Response<PropertyOverviewResponse> getPropertyOverView(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id) {
        return service.executeServiceCall(() -> {
            PropertyResponse propertyResponse = propertyService.findPropertyById(user, id);
            return PropertyOverviewResponse.fromPropertyResponse(propertyResponse);
        }, "Failed to fetch property overview");
    }

    @GetMapping("/{id}/view")
    public Response<PropertyResponse> getPropertyView(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id) {
        return service.executeServiceCall(() -> propertyService.findPropertyById(user, id),
                "Failed to fetch property");
    }

    @GetMapping("/landlord/all")
    public Response<List<PropertyOverviewResponse>> getAllLandlordProperties(
            @CurrentUser CustomUserDetails user,
            @RequestParam UUID landlordId) {
        return service.executeServiceCall(() -> propertyService.getLandlordProperties(user, landlordId),
                "Failed to fetch landlord properties");
    }

    @PatchMapping("/{id}/edit-contact")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN', 'SUPER_ADMIN')")
    public Response<PropertyResponse> editPropertyContact(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @Valid @RequestBody EditPropertyContactRequest request) {
        return service.executeServiceCall(() -> propertyService.editPropertyContact(user, id, request),
                "Failed to edit property contact");
    }

    @PatchMapping("/{id}/edit-info")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN', 'SUPER_ADMIN')")
    public Response<PropertyResponse> editPropertyInfo(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @Valid @RequestBody EditPropertyBasicRequest request) {
        return service.executeServiceCall(() -> propertyService.editPropertyInfo(user, id, request),
                "Failed to edit property info");
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN', 'SUPER_ADMIN')")
    public Response<PropertyResponse> deleteProperty(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id) {
        return service.executeServiceCall(() -> propertyService.deleteProperty(user, id),
                "Failed to delete property");
    }

    @Transactional
    @GetMapping("/recommended")
    public Response<List<PropertyResponse>> getRecommendedProperties(
            @CurrentUser CustomUserDetails user) {
        if (user == null || user.getUser() == null) {
            return Response.<List<PropertyResponse>>accessDenied()
                    .setErrors("You must login and set preferred location to get recommended properties.");
        }
        return service.executeServiceCall(() -> propertyService.getRecommendedProperties(user),
                "Failed to fetch recommended properties");
    }
}