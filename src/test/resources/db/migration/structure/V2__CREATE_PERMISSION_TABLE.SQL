create table if not exists permissions
(
    id   uuid not null
        primary key,
    name varchar(255)
        constraint unq_name
            unique
);

