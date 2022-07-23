create extension if not exists "pgcrypto";

drop table if exists "venue";
create table "venue"
(
    "id"         uuid primary key default gen_random_uuid(),
    "name"       text not null,
    "created_at" timestamp        default now(),
    "updated_at" timestamp        default now()
);

drop table if exists "manager";
create table "manager"
(
    "id"           uuid primary key default gen_random_uuid(),
    "email"        text not null unique,
    "password"     text             default null,
    "name"         text not null,
    "last_name"    text             default null,
    "role"         text not null,
    "photo_url"    text             default null,
    "age"          int              default null,
    "details"      jsonb            default null,
    "last_used_ip" text             default null,
    "status"       text not null    default 'ACTIVE',
    "created_at"   timestamp        default now(),
    "updated_at"   timestamp        default now()
);

create index "manager_account_status_idx" on manager(status);
