create table bookings
(
    amount_paid    double precision,
    booking_date   timestamp(6) not null,
    check_in_date  timestamp(6) not null,
    check_out_date timestamp(6),
    created_at     timestamp(6) with time zone,
    deleted_at     timestamp(6) with time zone,
    updated_at     timestamp(6) with time zone,
    created_by     uuid,
    deleted_by     uuid,
    id             uuid         not null
        primary key,
    room_id        uuid         not null
        constraint fkrgoycol97o21kpjodw1qox4nc
            references rooms,
    updated_by     uuid,
    user_id        uuid         not null
        constraint fkeyog2oic85xg7hsu2je2lx3s6
            references users,
    booking_status varchar(255) not null
        constraint bookings_booking_status_check
            check ((booking_status)::text = ANY
                   ((ARRAY ['PENDING'::character varying, 'CONFIRMED'::character varying, 'CANCELLED'::character varying, 'REJECTED'::character varying, 'COMPLETED'::character varying])::text[])),
    remark         oid,
    unique (booking_date, room_id)
);