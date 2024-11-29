create table if not exists properties_chars
(
    property_id       uuid not null
        constraint fk4ia3x8y9mtguu7y7snrvg5cd8
            references properties,
    char_id uuid not null
        constraint fkpkolsj8u6t8vv6r5oj9mowdi3
            references property_chars,
    constraint unq_property_charc
        primary key (property_id, char_id)
);
