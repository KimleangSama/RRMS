package com.kkimleang.rrms.controller.payment;

import com.kkimleang.rrms.annotation.*;
import com.kkimleang.rrms.controller.*;
import com.kkimleang.rrms.payload.*;
import com.kkimleang.rrms.payload.request.payment.*;
import com.kkimleang.rrms.payload.response.payment.*;
import com.kkimleang.rrms.service.payment.*;
import com.kkimleang.rrms.service.user.*;
import java.util.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contract")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;
    private final GlobalControllerServiceCall service;

    @PostMapping("/create")
    public Response<ContractResponse> createContract(@CurrentUser CustomUserDetails user, @RequestBody CreateContractRequest request) {
        return service.executeServiceCall(() -> contractService.createContract(user, request), "Failed to create contract");
    }

    @GetMapping("/of-property/{propertyId}")
    public Response<List<ContractResponse>> getContractsOfProperty(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID propertyId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return service.executeServiceCall(() -> contractService.getContractsOfProperty(user, propertyId, page, size),
                "Failed to fetch contracts of property");
    }
}
