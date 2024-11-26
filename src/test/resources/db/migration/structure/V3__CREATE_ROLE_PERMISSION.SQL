create table if not exists roles_permissions
(
    role_id       uuid not null
        constraint fkqi9odri6c1o81vjox54eedwyh
            references roles,
    permission_id uuid not null
        constraint fkbx9r9uw77p58gsq4mus0mec0o
            references permissions,
    constraint unq_role_permission
        primary key (role_id, permission_id)
);
