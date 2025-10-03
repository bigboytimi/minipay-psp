-- ================================
--  INIT MIGRATION
--  Creates all core tables
-- ================================

-- CHARGE SETTING TABLE
create table if not exists charge_setting_tbl
(


    id                     varchar(255)   not null
        primary key,
    fixed_fee              numeric(38, 2) not null,
    msc_cap                numeric(38, 2) not null,
    percentage_fee         numeric(38, 2) not null,
    platform_provider_cap  numeric(38, 2) not null,
    platform_provider_rate numeric(38, 2) not null,
    use_fixed_msc          boolean        not null,
    vat_rate               numeric(38, 2) not null,
    merchant_id            varchar(50)    not null
        constraint fk4b73x3eidm0ogm1ouut1qijj9
            references merchant_tbl,
    created_at             timestamp(6),
    updated_at             timestamp(6)
);

-- MERCHANT TABLE
create table if not exists merchant_tbl
(
    id                 varchar(50)  not null
        primary key,
    merchant_email     varchar(50)  not null
        unique,
    merchant_id        varchar(50)  not null
        unique,
    merchant_name      varchar(50)  not null,
    merchant_status    varchar(50)  not null
        constraint merchant_tbl_merchant_status_check
            check ((merchant_status)::text = ANY
                   ((ARRAY ['ACTIVE'::character varying, 'SUSPENDED'::character varying, 'INACTIVE'::character varying])::text[])),
    callback_url       varchar(255) not null,
    settlement_account varchar(50)  not null,
    settlement_bank    varchar(50)  not null,
    webhook_secret     varchar(255) not null,
    created_at         timestamp(6),
    updated_at         timestamp(6)
);

-- PAYMENT TABLE
create table if not exists payment_tbl
(
    id              varchar(50)    not null
        primary key,
    merchant_id     varchar(50)    not null
        constraint fkqcjgbm8uqi08si4j2ioc3liuq
            references merchant_tbl,
    customer_id     varchar(50)    not null,
    order_id        varchar(50)    not null
        unique,
    amount          numeric(38, 2) not null,
    amount_payable  numeric(38, 2) not null,
    currency        varchar(3)     not null
        constraint payment_tbl_currency_check
            check ((currency)::text = ANY
                   ((ARRAY ['NGN'::character varying, 'USD'::character varying, 'EUR'::character varying, 'GBP'::character varying])::text[])),
    msc             numeric(38, 2) not null,
    payable_vat     numeric(38, 2) not null,
    processor_fee   numeric(38, 2) not null,
    processor_vat   numeric(38, 2) not null,
    settled         boolean        not null,
    vat_amount      numeric(38, 2) not null,
    payment_channel varchar(50)    not null
        constraint payment_tbl_payment_channel_check
            check ((payment_channel)::text = ANY
                   ((ARRAY ['CARD'::character varying, 'WALLET'::character varying, 'BANK_TRANSFER'::character varying])::text[])),
    payment_ref     varchar(50)    not null
        unique,
    payment_status  varchar(50)    not null
        constraint payment_tbl_payment_status_check
            check ((payment_status)::text = ANY
                   ((ARRAY ['PENDING'::character varying, 'SUCCESS'::character varying, 'FAILED'::character varying])::text[])),
    created_at      timestamp(6),
    settled_at      timestamp(6)   not null,
    updated_at      timestamp(6)
);

-- ROLE TABLE
create table if not exists role_tbl
(

    id         varchar(50) not null
        primary key,
    role_name  varchar(50) not null
        constraint role_tbl_role_name_check
            check ((role_name)::text = ANY
                   ((ARRAY ['ADMIN'::character varying, 'MAKER'::character varying, 'CHECKER'::character varying, 'MERCHANT_USER'::character varying])::text[])),
    created_at timestamp(6),
    updated_at timestamp(6)
);


-- SETTLEMENT BATCH TABLE
create table if not exists settlement_batch_tbl
(
    id             varchar(50)    not null
        primary key,
    merchant_id    varchar(50)    not null
        constraint fkaxio25sn48o1bysx3soumpnw6
            references merchant_tbl,
    settlement_ref varchar(50)    not null
        unique,
    status         varchar(50)    not null
        constraint settlement_batch_tbl_status_check
            check ((status)::text = ANY ((ARRAY ['PENDING'::character varying, 'POSTED'::character varying])::text[])),
    amount         numeric(38, 2) not null,
    amount_payable numeric(38, 2) not null,
    income         numeric(38, 2) not null,
    msc            numeric(38, 2) not null,
    payable_vat    numeric(38, 2) not null,
    period_end     date           not null,
    period_start   date           not null,
    processor_fee  numeric(38, 2) not null,
    processor_vat  numeric(38, 2) not null,
    vat_amount     numeric(38, 2) not null,
    count          bigint         not null,
    created_at     timestamp(6),
    updated_at     timestamp(6)
);

-- SETTLEMENT ITEM TABLE
create table if not exists settlement_item_tbl
(

    id             varchar(50)    not null
        primary key,
    payment_id     varchar(50)    not null
        constraint fk62t0nryck524i18ibqn2gxtkg
            references payment_tbl,
    amount         numeric(38, 2) not null,
    amount_payable numeric(38, 2) not null,
    msc            numeric(38, 2) not null,
    processor_fee  numeric(38, 2) not null,
    processor_vat  numeric(38, 2) not null,
    vat_amount     numeric(38, 2) not null,
    created_at     timestamp(6),
    updated_at     timestamp(6),
    batch_id       varchar(50)    not null
        constraint fkqot32kjn6wy00iq8hy1tgu21m
            references settlement_batch_tbl
);

-- USER ROLE TABLE
create table if not exists user_role_tbl
(
    role_id varchar(50) not null
        unique,
    user_id varchar(50) not null
        primary key
);

create table if not exists user_tbl
(
    id            varchar(50)  not null
        primary key,
    merchant_id   varchar(50)
        constraint fkx291qiuwnr546x1c2ilhvbh0
            references merchant_tbl,
    password_hash varchar(255) not null,
    email         varchar(50)  not null
        unique,
    status        varchar(50)  not null
        constraint user_tbl_status_check
            check ((status)::text = ANY
                   ((ARRAY ['ACTIVE'::character varying, 'SUSPENDED'::character varying, 'INACTIVE'::character varying])::text[])),
    username      varchar(50)  not null
        unique,
    created_at    timestamp(6),
    updated_at    timestamp(6)
);

CREATE INDEX idx_payment_merchant_id ON payment_tbl(merchant_id);
CREATE INDEX idx_payment_payment_ref ON payment_tbl(payment_ref);
CREATE INDEX idx_payment_status ON payment_tbl(payment_status);
CREATE INDEX idx_settlement_batch_merchant_id ON settlement_batch_tbl(merchant_id);
CREATE INDEX idx_settlement_batch_status ON settlement_batch_tbl(status);
CREATE INDEX idx_settlement_batch_settlement_ref ON settlement_batch_tbl(settlement_ref);


CREATE INDEX idx_merchant_merchant_id ON merchant_tbl(merchant_id);
CREATE INDEX idx_user_username ON user_tbl(username)
