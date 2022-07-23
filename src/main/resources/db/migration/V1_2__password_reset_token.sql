create table "password_reset_token"
(
    "token"      text    not null primary key,
    "account_id" uuid    not null,
    "active"     boolean not null default false,
    "created_at" timestamp        default now(),
    "updated_at" timestamp        default now()
);
