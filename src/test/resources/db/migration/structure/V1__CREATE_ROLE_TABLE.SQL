CREATE TABLE IF NOT EXISTS roles (
                                     id   uuid         not null primary key,
                                     name varchar(255) not null
                                         constraint unq_role_name unique
);
