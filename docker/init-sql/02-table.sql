-- auto-generated definition
create table hdb.rss_item
(
    id          bigint generated by default as identity
        primary key,
    created_at  timestamp(6),
    updated_at  timestamp(6),
    category    varchar(255),
    link        varchar(255),
    magnet_link varchar(255),
    title       varchar(255)
);

create table hdb.rss_setting
(
    key        varchar(255) not null
        primary key,
    created_at timestamp(6),
    updated_at timestamp(6),
    value      varchar(255)
);