create table prop_room_pictures
(
    id              uuid         not null
        primary key,
    created_at      timestamp(6) with time zone,
    created_by      uuid,
    deleted_at      timestamp(6) with time zone,
    deleted_by      uuid,
    updated_at      timestamp(6) with time zone,
    updated_by      uuid,
    filename        varchar(255) not null
        constraint unq_filename
            unique,
    accessible_type varchar(255) not null
        constraint prop_room_pictures_accessible_type_check
            check ((accessible_type)::text = ANY
                   ((ARRAY ['PUBLIC'::character varying, 'OWNER'::character varying, 'PRIVATE'::character varying])::text[])),
    location_type   varchar(255) not null
        constraint prop_room_pictures_location_type_check
            check ((location_type)::text = ANY
                   ((ARRAY ['LOCAL'::character varying, 'S3'::character varying, 'FIREBASE'::character varying])::text[])),
    prop_id         uuid
        constraint fkf9tqprm35wjkjn3c280w8csof
            references properties,
    room_id         uuid
        constraint fkex8es8jtl8uh4x0x9qjnflpuo
            references rooms
);