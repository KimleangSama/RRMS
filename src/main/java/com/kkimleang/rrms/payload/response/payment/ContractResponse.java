package com.kkimleang.rrms.payload.response.payment;

import com.kkimleang.rrms.config.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.enums.room.*;
import com.kkimleang.rrms.util.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
@Setter
@ToString
public class ContractResponse {
    private String name;
    private LocalDateTime signedDate;
    private LocalDateTime expiryDate;
    private String contractFileUrl;
    private ContractStatus contractStatus;
    private UUID roomAssignmentId;

    private UUID tenantId;
    private String tenantFullname;

    private UUID landlordId;
    private String landlordFullname;

    private UUID roomId;
    private String roomNumber;

    public static ContractResponse fromContract(Contract contract) {
        DeletableEntityValidator.validate(contract, "Contract");
        ContractResponse response = new ContractResponse();
        ModelMapperConfig.modelMapper().map(contract, response);
        response.setRoomAssignmentId(contract.getRoomAssignment().getId());
        response.setTenantId(contract.getRoomAssignment().getUser().getId());
        response.setTenantFullname(contract.getRoomAssignment().getUser().getFullname());
        response.setLandlordId(contract.getRoomAssignment().getRoom().getProperty().getUser().getId());
        response.setLandlordFullname(contract.getRoomAssignment().getRoom().getProperty().getUser().getFullname());
        response.setRoomId(contract.getRoomAssignment().getRoom().getId());
        response.setRoomNumber(contract.getRoomAssignment().getRoom().getRoomNumber());
        return response;
    }

    public static List<ContractResponse> fromContracts(List<Contract> content) {
        List<ContractResponse> responses = new ArrayList<>();
        for (Contract contract : content) {
            try {
                responses.add(fromContract(contract));
            } catch (Exception e) {
                log.debug("Contract {} is deleted at {}", contract.getId(), contract.getDeletedAt());
            }
        }
        return responses;
    }
}
