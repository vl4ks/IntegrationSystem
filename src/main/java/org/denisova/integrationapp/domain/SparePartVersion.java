package org.denisova.integrationapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "spare_part_versions")
public class SparePartVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spare_code", nullable = false, length = 128)
    private String spareCode;

    @Column(name = "name", length = 512)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "type", length = 64)
    private String type;

    @Column(name = "status", length = 64)
    private String status;

    @Column(name = "price", precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "version_created_at", insertable = false, updatable = false)
    private OffsetDateTime versionCreatedAt;

    @Column(name = "change_kind", length = 16)
    private String changeKind; // CREATED | UPDATED | DEACTIVATED
}
