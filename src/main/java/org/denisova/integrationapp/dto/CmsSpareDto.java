package org.denisova.integrationapp.dto;

import lombok.Getter;
import lombok.Setter;


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
    private Integer price;
    private Integer quantity;
    private String updatedAt;
}
