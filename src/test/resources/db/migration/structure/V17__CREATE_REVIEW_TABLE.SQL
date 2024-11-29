create table if not exists reviews
(
    id          uuid         not null
        primary key,
    created_at  timestamp(6) with time zone,
    created_by  uuid,
    deleted_at  timestamp(6) with time zone,
    deleted_by  uuid,
    updated_at  timestamp(6) with time zone,
    updated_by  uuid,
    comment     varchar(255) not null,
    rating      varchar(255) not null,
    review_date timestamp(6) not null,
    visible     boolean,
    property_id uuid         not null
        constraint fk83j25x4ukm1vhf1336h1tt882
            references properties,
    user_id     uuid         not null
        constraint fkcgy7qjc1r99dp117y9en6lxye
            references users,
    constraint ukpwqwn557m3tx0kkrh9b70smd0
        unique (property_id, user_id)
);
