# Integration App (CMS → Report)

Spring Boot + Apache Camel приложение, которое:
- периодически тянет данные о запчастях из CMS и складывает в бд;
- ведет историю изменений;
- формирует CSV-отчет и отправляет его в проверяющую систему.

---

## Технологии
- Java 17, Spring Boot 3.2
- Apache Camel 4.4 (routes: timer/http/sql/file/jackson/csv/log/direct)
- БД: PostgreSQL

---

## Основные маршруты
- `CmsSyncRoute` (timer 5 мин): GET CMS `/students/{id}/cms/spares?page=&size=` → upsert в `spare_parts`, запись версии в `spare_part_versions`, журнал `sync_run`, деактивация отсутствующих записей.
- `SpareProcessingRoute`: insert/update `spare_parts`, добавляет запись в `spare_part_versions`, собирает статистику.
- `DeactivationRoute`: помечает не пришедшие в текущем синке как неактивные и пишет версию с `change_kind=DEACTIVATED`.
- `StatsRoute`/`SyncRunUpdateRoute`: агрегируют counters и проставляют `finished_at`/`status` в `sync_run`.
- `ReportGenerationRoute` (timer 1 час): берет данные из таблицы `spare_parts`, генерирует CSV, отправляет в Report API и сохраняет ответ в `reports/responses`.

---

## Схема данных
- `spare_parts`: spare_code, name, description, type, status, price, quantity, updated_at, is_active, last_seen_at, created_at, modified_at.
- `spare_part_versions`: история изменений, поле `change_kind` (CREATED/UPDATED/DEACTIVATED).
- `sync_run`: журнал запусков синхронизации и статистика.

---

## Конфигурация
`src/main/resources/application.properties`:
- `spring.datasource.*` — подключение к Postgres.
- `camel.component.sql.data-source=#dataSource` — Camel SQL использует бин DataSource.
- `app.cms.base-url`, `app.report.base-url`, `app.cms.page-size`, `app.student-id`.
- Таймеры: `camel.route.cms-sync.period` (5 мин), `camel.route.report-gen.period` (1 ч).

---

## Пример CSV-строки

```csv
SPARE-1;Spare Part 1;Description for spare part 1;CLUTCH;DAMAGED;11;46;2025-10-07T15:25:34.861286586
```
