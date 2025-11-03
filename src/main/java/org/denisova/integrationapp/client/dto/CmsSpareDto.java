package org.denisova.integrationapp.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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
    private OffsetDateTime updatedAt;
}
