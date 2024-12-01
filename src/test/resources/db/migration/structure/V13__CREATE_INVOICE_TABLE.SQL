create table if not exists invoices
(
    id                 uuid             not null
        primary key,
    created_at         timestamp(6) with time zone,
    created_by         uuid,
    deleted_at         timestamp(6) with time zone,
    deleted_by         uuid,
    updated_at         timestamp(6) with time zone,
    updated_by         uuid,
    amount_due         double precision not null,
    amount_paid        double precision not null,
    discount           double precision not null,
    due_date           timestamp(6)     not null,
    invoice_date       timestamp(6)     not null,
    invoice_status     smallint         not null
        constraint invoices_invoice_status_check
            check ((invoice_status >= 0) AND (invoice_status <= 4)),
    remark             oid              not null,
    total_amount       double precision not null,
    room_assignment_id uuid             not null
        constraint fkndh9ab9jd83xnkdmkgoha9vrc
            references room_assignments,
    tenant_id            uuid             not null
        constraint fkbwr4d4vyqf2bkoetxtt8j9dx7
            references users,
    constraint uklby6sr03rydk5p6byohwpvb2e
        unique (room_assignment_id, invoice_date),
    constraint ukc1bjgs9ofdvvjuha0fpw2vbwp
        unique (room_assignment_id, due_date)
);