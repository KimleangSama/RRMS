create table if not exists payments
(
    id             uuid             not null
        primary key,
    created_at     timestamp(6) with time zone,
    created_by     uuid,
    deleted_at     timestamp(6) with time zone,
    deleted_by     uuid,
    updated_at     timestamp(6) with time zone,
    updated_by     uuid,
    expected_amount    double precision,
    amount_paid    double precision not null,
    payment_date   timestamp(6)     not null,
    payment_method smallint         not null
        constraint payments_payment_method_check
            check ((payment_method >= 0) AND (payment_method <= 3)),
    payment_status smallint         not null
        constraint payments_payment_status_check
            check ((payment_status >= 0) AND (payment_status <= 5)),
    invoice_id     uuid             not null
        constraint fkrbqec6be74wab8iifh8g3i50i
            references invoices
);