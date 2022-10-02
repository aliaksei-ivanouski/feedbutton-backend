drop table if exists "user";

create table "user"
(
    "id"               uuid primary key default gen_random_uuid(),
    "email"            text not null unique,
    "password"         text,
    "name"             text,
    "phone_number"     text,
    "photo_url"        text,
    "points"           int  not null    default 0,
    "registration_ip"  inet,
    "last_used_ip"     inet,
    "unsubscribed"     boolean          default false,
    "last_check_in_at" timestamp,
    "status"           text not null    default 'INACTIVE',
    "created_at"       timestamp        default now(),
    "updated_at"       timestamp        default now()
);

create index "user_email_idx" on "user"("email");
create index "user_phone_number_idx" on "user"("phone_number");
create index "user_status_idx" on "user"("status");
