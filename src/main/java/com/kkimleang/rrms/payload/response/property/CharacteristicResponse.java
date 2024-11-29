package com.kkimleang.rrms.payload.response.property;

import com.kkimleang.rrms.config.ModelMapperConfig;
import com.kkimleang.rrms.entity.PropertyChars;
import lombok.*;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CharacteristicResponse implements Serializable {
    private UUID id;
    private String name;
    private String description;

    public static Set<CharacteristicResponse> fromCharacteristics(Set<PropertyChars> characteristics) {
        return characteristics.stream().map(characteristic -> {
            CharacteristicResponse characteristicResponse = new CharacteristicResponse();
            ModelMapperConfig.modelMapper().map(characteristic, characteristicResponse);
            return characteristicResponse;
        }).collect(java.util.stream.Collectors.toSet());
    }
}
