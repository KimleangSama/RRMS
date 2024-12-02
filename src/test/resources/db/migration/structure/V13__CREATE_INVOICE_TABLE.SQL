create table invoices
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
    amount_paid        double precision,
    discount           double precision,
    due_date           timestamp(6)     not null,
    invoice_date       timestamp(6)     not null,
    invoice_status     varchar(255)     not null
        constraint invoices_invoice_status_check
            check ((invoice_status)::text = ANY
                   ((ARRAY ['UNPAID'::character varying, 'PAID'::character varying, 'CANCELLED'::character varying, 'EXPIRED'::character varying, 'REFUNDED'::character varying])::text[])),
    remark             oid              not null,
    total_amount       double precision not null,
    room_assignment_id uuid             not null
        constraint fkndh9ab9jd83xnkdmkgoha9vrc
            references room_assignments
);