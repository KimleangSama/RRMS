create table if not exists users
(
    id                     uuid         not null
        primary key,
    created_at             timestamp(6) with time zone,
    created_by             uuid,
    deleted_at             timestamp(6) with time zone,
    deleted_by             uuid,
    updated_at             timestamp(6) with time zone,
    updated_by             uuid,
    address_proof          varchar(255),
    assignment_code        varchar(255)
        constraint unq_assignment_code
            unique,
    bank_account_name      varchar(255),
    bank_account_number    varchar(255)
        constraint unq_bank_account_number
            unique,
    bank_account_picture   varchar(255),
    date_of_birth          date,
    email                  varchar(255) not null,
    emergency_contact      varchar(255),
    emergency_relationship varchar(255),
    fullname               varchar(255),
    gender                 varchar(255) not null
        constraint users_gender_check
            check ((gender)::text = ANY
                   ((ARRAY ['MALE'::character varying, 'FEMALE'::character varying, 'NOT_SAID'::character varying])::text[])),
    id_card_back           varchar(255),
    id_card_front          varchar(255),
    last_login_at          timestamp(6) with time zone,
    national_id            varchar(255)
        constraint unq_national_id
            unique,
    nationality            varchar(255),
    password               varchar(255),
    phone_number           varchar(255)
        constraint unq_phone_number
            unique,
    preferred_language     varchar(255),
    preferred_location     varchar(255),
    preferred_latitude     double precision,
    preferred_longitude    double precision,
    preferred_radius       double precision,
    profile_picture        varchar(255),
    provider               varchar(255) not null
        constraint users_provider_check
            check ((provider)::text = ANY
                   ((ARRAY ['LOCAL'::character varying, 'FACEBOOK'::character varying, 'GOOGLE'::character varying, 'GITHUB'::character varying])::text[])),
    user_status            varchar(255) not null
        constraint users_user_status_check
            check ((user_status)::text = ANY
                   ((ARRAY ['PENDING'::character varying, 'ACTIVE'::character varying, 'INACTIVE'::character varying, 'CLOSED'::character varying, 'BLOCKED'::character varying])::text[])),
    username               varchar(255) not null
        constraint unq_username
            unique,
    verified               boolean,
    verify_code            varchar(255)
        constraint unq_verify_code
            unique
);