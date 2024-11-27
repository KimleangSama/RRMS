create table if not exists property_chars
(
    id          uuid         not null
        primary key,
    description varchar(255) not null,
    name        varchar(255) not null
        constraint ukltit14mwxfa0r2sir4fsplihj
            unique
);
