package org.denisova.integrationapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;


/**
 * Текущее состояние детали в системе.
 * Поля соответствуют бизнес-сущности, актуальные на последний синк.
 */
@Getter
@Setter
@Entity
@Table(name = "spare_parts")
public class SparePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spare_code", nullable = false, unique = true, length = 128)
    private String spareCode;

    @Column(name = "name", length = 512)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "type", length = 64)
    private String type;

    @Column(name = "status", length = 64)
    private String status;

    @Column(name = "price")
    private Integer price;

    @Column(name = "quantity")
    private Integer quantity;

    /** Метка последнего обновления из CMS (строка в исходном формате). */
    @Column(name = "updated_at")
    private String updatedAt;

    /** Флаг актуальности записи (деактивация — если деталь исчезла из CMS). */
    @Column(name = "is_active")
    private Boolean isActive = Boolean.TRUE;

    /** Когда эта запись в последний раз была замечена текущим синком. */
    @Column(name = "last_seen_at")
    private OffsetDateTime lastSeenAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "modified_at")
    private OffsetDateTime modifiedAt;

    @PreUpdate
    void preUpdate() {
        this.modifiedAt = OffsetDateTime.now();
    }
}
