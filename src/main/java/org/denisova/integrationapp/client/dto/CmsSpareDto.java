package org.denisova.integrationapp.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Нормализованный DTO из CMS для апсёрта.
 * Используется для записи в БД.
 */
@Getter
@Setter
public class CmsSpareDto {
    private String spareCode;
    private String spareName;
    private String spareDescription;
    private String spareType;
    private String spareStatus;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime updatedAt;
}
