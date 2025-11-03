package org.denisova.integrationapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "sync_run")
public class SyncRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "status", length = 16)
    private String status; // STARTED, OK, ERROR

    @Column(name = "total_from_cms")
    private Integer totalFromCms;

    @Column(name = "inserted")
    private Integer inserted;

    @Column(name = "updated")
    private Integer updated;

    @Column(name = "deactivated")
    private Integer deactivated;

    @Column(name = "message")
    private String message;
}
