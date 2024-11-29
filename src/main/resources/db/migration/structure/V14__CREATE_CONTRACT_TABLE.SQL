create table if not exists contracts
(
    id                uuid         not null
        primary key,
    created_at        timestamp(6) with time zone,
    created_by        uuid,
    deleted_at        timestamp(6) with time zone,
    deleted_by        uuid,
    updated_at        timestamp(6) with time zone,
    updated_by        uuid,
    contract_file_url varchar(255) not null,
    contract_status   varchar(255) not null
        constraint contracts_contract_status_check
            check ((contract_status)::text = ANY
                   ((ARRAY ['PENDING'::character varying, 'ACTIVE'::character varying, 'EXPIRED'::character varying, 'CANCELLED'::character varying])::text[])),
    expiry_date       timestamp(6),
    signed_date       timestamp(6) not null,
    room_id           uuid         not null
        constraint fkju1b0xobla9t8oexrb8lpi8jq
            references rooms,
    user_id           uuid         not null
        constraint fkq3v8dxlubujug7dxvpauig94n
            references users,
    constraint ukrr89f49qyt2q8iwx6id1mb4bi
        unique (room_id, user_id)
);
