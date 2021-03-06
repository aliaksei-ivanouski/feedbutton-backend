drop table if exists "manager_venue";
create table manager_venue
(
    "manager_id"    uuid references manager ("id"),
    "venue_id"      uuid references venue ("id"),
    "access_scopes" text[] not null default '{}',
    constraint manager_account_venue_pkey primary key ("manager_id", "venue_id")
)
