package org.denisova.integrationapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Журнал одного запуска синхронизации.
 * Хранит метки времени, статус и агрегированную статистику.
 */
@Getter
@Setter
@Entity
@Table(name = "sync_run")
public class SyncRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Время старта прогона. */
    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    /** Время завершения прогона. */
    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "status", length = 16)
    private String status; // STARTED, OK, ERROR

    /** Сколько записей пришло из CMS суммарно. */
    @Column(name = "total_from_cms")
    private Integer totalFromCms;

    /** Сколько вставлено новых. */
    @Column(name = "inserted")
    private Integer inserted;

    /** Сколько обновлено существующих. */
    @Column(name = "updated")
    private Integer updated;

    /** Сколько деактивировано. */
    @Column(name = "deactivated")
    private Integer deactivated;

    /** Текст ошибки/заметки по прогону (если есть). */
    @Column(name = "message")
    private String message;
}
