create table if not exists rooms
(
    id               uuid             not null
        primary key,
    created_at       timestamp(6) with time zone,
    created_by       uuid,
    deleted_at       timestamp(6) with time zone,
    deleted_by       uuid,
    updated_at       timestamp(6) with time zone,
    updated_by       uuid,
    available_date   date             not null,
    available_status varchar(255)     not null
        constraint rooms_available_status_check
            check ((available_status)::text = ANY
                   ((ARRAY ['AVAILABLE'::character varying, 'NOT_AVAILABLE'::character varying, 'RESERVED'::character varying, 'BOOKED'::character varying, 'CANCELLED'::character varying, 'CHECKED_IN'::character varying, 'CHECKED_OUT'::character varying, 'PENALIZED'::character varying, 'CLOSED'::character varying, 'MAINTENANCE'::character varying])::text[])),
    rental_price     double precision not null,
    room_floor       integer          not null,
    room_number      varchar(255)     not null,
    room_size        varchar(255)     not null
        constraint rooms_room_size_check
            check ((room_size)::text = ANY
                   ((ARRAY ['THREE_BY_THREE'::character varying, 'THREE_BY_FOUR'::character varying, 'THREE_BY_FIVE'::character varying, 'THREE_BY_SIX'::character varying, 'THREE_BY_SEVEN'::character varying, 'FOUR_BY_FOUR'::character varying, 'FOUR_BY_FIVE'::character varying, 'FOUR_BY_SIX'::character varying, 'FOUR_BY_SEVEN'::character varying, 'FIVE_BY_FIVE'::character varying, 'FIVE_BY_SIX'::character varying, 'FIVE_BY_SEVEN'::character varying, 'SIX_BY_SIX'::character varying, 'SIX_BY_SEVEN'::character varying, 'SEVEN_BY_SEVEN'::character varying, 'OTHER'::character varying])::text[])),
    room_type        varchar(255)     not null
        constraint rooms_room_type_check
            check ((room_type)::text = ANY
                   ((ARRAY ['SINGLE'::character varying, 'DOUBLE'::character varying, 'STUDIO'::character varying, 'MEZZANINE'::character varying])::text[])),
    property_id      uuid             not null
        constraint fk35r032kwh410ggyqcbqrnhcut
            references properties,
    constraint uk7s0egtid5n0bnl3bp5gky363p
        unique (room_number, property_id)
);
