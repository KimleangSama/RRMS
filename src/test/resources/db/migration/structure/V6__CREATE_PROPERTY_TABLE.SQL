create table if not exists properties
(
    id               uuid         not null
        primary key,
    created_at       timestamp(6) with time zone,
    created_by       uuid,
    deleted_at       timestamp(6) with time zone,
    deleted_by       uuid,
    updated_at       timestamp(6) with time zone,
    updated_by       uuid,
    address_gmap     varchar(255) not null,
    address_proof    varchar(255) not null,
    commune          varchar(255),
    contact          varchar(255),
    description      oid,
    district         varchar(255),
    email            varchar(255),
    website          varchar(255),
    latitude         double precision,
    longitude        double precision,
    name             varchar(255) not null,
    penalized_reason varchar(255),
    picture_cover    varchar(255) not null,
    property_status  varchar(255) not null
        constraint properties_property_status_check
            check ((property_status)::text = ANY
                   ((ARRAY ['PENDING'::character varying, 'ACTIVE'::character varying, 'DRAFT'::character varying, 'INREVIEW'::character varying, 'CLOSED'::character varying, 'PENALIZED'::character varying])::text[])),
    property_type    varchar(255) not null
        constraint properties_property_type_check
            check ((property_type)::text = ANY
                   ((ARRAY ['HOUSE'::character varying, 'APARTMENT'::character varying, 'CONDO'::character varying])::text[])),
    province         varchar(255),
    village          varchar(255),
    zip_code         varchar(255),
    user_id          uuid         not null
        constraint fktnyxik10x9mx26017sjnuo2nw
            references users
);
