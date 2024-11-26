create table if not exists properties_characteristics
(
    property_id       uuid not null
        constraint fk4ia3x8y9mtguu7y7snrvg5cd8
            references properties,
    characteristic_id uuid not null
        constraint fkpkolsj8u6t8vv6r5oj9mowdi3
            references property_characteristics,
    constraint unq_property_characteristic
        primary key (property_id, characteristic_id)
);
