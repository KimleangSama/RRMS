create table if not exists room_pictures
(
    id          uuid         not null
        primary key,
    created_at  timestamp(6) with time zone,
    created_by  uuid,
    deleted_at  timestamp(6) with time zone,
    deleted_by  uuid,
    updated_at  timestamp(6) with time zone,
    updated_by  uuid,
    picture_url varchar(255) not null,
    room_id     uuid         not null
        constraint fkj8m9f62vv5mkt2fmk52sm3s8r
            references rooms
);
