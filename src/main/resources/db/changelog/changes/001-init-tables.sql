-- liquibase formatted sql
-- changeset denisova:2025-11-04-init-tables
-- comment: Инициализация таблиц хранилища запчастей: текущее состояние, версии изменений и журнал синхронизаций.

-- spare_parts: текущее состояние детали
create table spare_parts (
                             id               bigserial                 not null,
                             spare_code       varchar(128)              not null,
                             name             varchar(512)              null,
                             description      text                      null,
                             type             varchar(64)               null,
                             status           varchar(64)               null,
                             price            int                       null,
                             quantity         int                       null,
                             updated_at       text                      null,
                             is_active        boolean                   not null default true,
                             last_seen_at     timestamptz               null,

    -- служебные
                             created_at       timestamptz               not null default now(),
                             modified_at      timestamptz               null,

                             primary key (id),
                             unique (spare_code)
);

-- spare_part_versions: полная история изменений
create table spare_part_versions (
                                     id                  bigserial             not null,
                                     spare_code          varchar(128)          not null,
                                     name                varchar(512)          null,
                                     description         text                  null,
                                     type                varchar(64)           null,
                                     status              varchar(64)           null,
                                     price               int                   null,
                                     quantity            int                   null,
                                     updated_at          text                  null,
                                     version_created_at  timestamptz           not null default now(),
                                     change_kind         varchar(16)           null,  -- CREATED | UPDATED | DEACTIVATED

                                     primary key (id)
);

create index idx_spare_part_versions_code on spare_part_versions(spare_code);

-- sync_run: журнал запусков синхронизации
create table sync_run (
                          id             bigserial                 not null,
                          started_at     timestamptz               null,
                          finished_at    timestamptz               null,
                          status         varchar(16)               null,   -- STARTED | OK | ERROR
                          total_from_cms int                       null,
                          inserted       int                       null,
                          updated        int                       null,
                          deactivated    int                       null,
                          message        text                      null,

                          primary key (id)
);

-- rollback drop table if exists sync_run;
-- rollback drop index if exists idx_spare_part_versions_code;
-- rollback drop table if exists spare_part_versions;
-- rollback drop table if exists spare_parts;
