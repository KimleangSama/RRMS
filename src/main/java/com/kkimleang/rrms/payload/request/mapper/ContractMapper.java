package com.kkimleang.rrms.payload.request.mapper;

import com.kkimleang.rrms.config.*;
import com.kkimleang.rrms.entity.*;
import com.kkimleang.rrms.payload.request.payment.*;
import lombok.extern.slf4j.*;

@Slf4j
public class ContractMapper {
    public static void createContractFromContractRequest(
            Contract contract,
            CreateContractRequest request
    ) {
        ModelMapperConfig.modelMapper().map(request, contract);

    }
}
