create table payments
(
    amount_paid    double precision not null,
    created_at     timestamp(6) with time zone,
    deleted_at     timestamp(6) with time zone,
    payment_date   timestamp(6)     not null,
    updated_at     timestamp(6) with time zone,
    created_by     uuid,
    deleted_by     uuid,
    id             uuid             not null
        primary key,
    invoice_id     uuid             not null
        constraint fkrbqec6be74wab8iifh8g3i50i
            references invoices,
    updated_by     uuid,
    payment_method varchar(255)     not null
        constraint payments_payment_method_check
            check ((payment_method)::text = ANY
                   ((ARRAY ['CASH'::character varying, 'BANK'::character varying, 'TRANSFER'::character varying, 'OTHER'::character varying])::text[])),
    remark         varchar(255)
);