create extension if not exists "pgcrypto";

drop table if exists "venue";
create table "venue"
(
    "id"         bigserial primary key not null,
    "name"       text                  not null,
    "created_at" timestamp default now(),
    "updated_at" timestamp default now()
);

drop table if exists "manager";
create table "manager"
(
    "id"           bigserial primary key not null,
    "venue_id"     bigint references venue ("id"),
    "email"        text               not null unique,
    "password"     text                        default null,
    "name"         text               not null,
    "last_name"    text               not null,
    "role"         text               not null,
    "photo_url"    text                        default null,
    "active"       boolean            not null default false,
    "age"          int                         default null,
    "details"      jsonb                       default null,
    "last_used_ip" text                        default null,
    "created_at"   timestamp                   default now(),
    "updated_at"   timestamp                   default now()
);
