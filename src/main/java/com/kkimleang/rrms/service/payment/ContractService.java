package com.kkimleang.rrms.service.payment;

import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.request.mapper.*;
import com.kkimleang.rrms.payload.request.payment.*;
import com.kkimleang.rrms.payload.response.payment.*;
import com.kkimleang.rrms.repository.payment.*;
import com.kkimleang.rrms.repository.property.*;
import com.kkimleang.rrms.repository.room.*;
import com.kkimleang.rrms.service.room.*;
import com.kkimleang.rrms.service.user.*;
import com.kkimleang.rrms.util.*;
import jakarta.transaction.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final PropertyRepository propertyRepository;
    private final RoomService roomService;
    private final RoomAssignmentRepository roomAssignmentRepository;
    private final RoomAssignmentService roomAssignmentService;

    @Transactional
    public ContractResponse createContract(CustomUserDetails user, CreateContractRequest request) {
        DeletableEntityValidator.validateUser(user);
        RoomAssignment ra = roomAssignmentRepository.findById(request.getRoomAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Room Assignment", "id" + request.getRoomAssignmentId()));
        DeletableEntityValidator.validate(ra, "Room Assignment");
        Contract contract = new Contract();
        ContractMapper.createContractFromContractRequest(contract, request);
        contract.setCreatedBy(user.getUser().getId());
        contract.setCreatedAt(Instant.now());
        contract.setRoomAssignment(ra);
        contract = contractRepository.save(contract);
        return ContractResponse.fromContract(contract);
    }

    @Transactional
    public List<ContractResponse> getContractsOfProperty(CustomUserDetails user, UUID propertyId, int page, int size) {
        DeletableEntityValidator.validateUser(user);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "id" + propertyId));
        DeletableEntityValidator.validate(property, "Property");
        List<Room> rooms = roomService.findRoomsByPropertyId(propertyId);
        List<RoomAssignment> roomAssignments = roomAssignmentService.findRoomAssignmentsByRooms(rooms);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contract> contracts = contractRepository.findByRoomAssignmentIn(roomAssignments, pageable);
        return ContractResponse.fromContracts(contracts.getContent());
    }
}
