package com.kkimleang.rrms.payload.request.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
public class EditPropertyCharacteristicRequest {
    private Set<UUID> characteristics;
}
