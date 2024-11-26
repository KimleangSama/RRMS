create table if not exists property_pictures
(
    id          uuid         not null
        primary key,
    created_at  timestamp(6) with time zone,
    created_by  uuid,
    deleted_at  timestamp(6) with time zone,
    deleted_by  uuid,
    updated_at  timestamp(6) with time zone,
    updated_by  uuid,
    picture_url varchar(255) not null
        constraint unq_picture_url
            unique,
    property_id uuid         not null
        constraint fkm1yxiwc2ihkglw7isidsyl5g2
            references properties
);