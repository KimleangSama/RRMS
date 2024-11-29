create table if not exists bookings
(
    id             uuid         not null
        primary key,
    created_at     timestamp(6) with time zone,
    created_by     uuid,
    deleted_at     timestamp(6) with time zone,
    deleted_by     uuid,
    updated_at     timestamp(6) with time zone,
    updated_by     uuid,
    amount_paid    double precision,
    booking_date   timestamp(6) not null,
    booking_status varchar(255) not null
        constraint bookings_booking_status_check
            check ((booking_status)::text = ANY
                   ((ARRAY ['PENDING'::character varying, 'CONFIRMED'::character varying, 'CANCELLED'::character varying, 'REJECTED'::character varying, 'COMPLETED'::character varying])::text[])),
    check_in_date  timestamp(6) not null,
    check_out_date timestamp(6),
    payment_status varchar(255) not null
        constraint bookings_payment_status_check
            check ((payment_status)::text = ANY
                   ((ARRAY ['PAID'::character varying, 'UNPAID'::character varying, 'PARTIAL_PAID'::character varying, 'CANCELLED'::character varying, 'REFUNDED'::character varying, 'OTHER'::character varying])::text[])),
    remark         oid,
    room_id        uuid         not null
        constraint fkrgoycol97o21kpjodw1qox4nc
            references rooms,
    user_id        uuid         not null
        constraint fkeyog2oic85xg7hsu2je2lx3s6
            references users,
    constraint ukegdmkwi560768wfitupsfqcwd
        unique (booking_date, room_id),
    constraint ukb6bun6gf0mr86q3cqj33why6j
        unique (room_id, user_id)
);
