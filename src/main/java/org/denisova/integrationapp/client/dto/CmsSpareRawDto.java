package org.denisova.integrationapp.client.dto;

/**
 * DTO под CSV: строки/значения, как их отдаёт CMS.
 * Для формирования CSV идентичного источнику (без форматирования).
 */
public class CmsSpareRawDto {
    public String spareCode;
    public String spareName;
    public String spareDescription;
    public String spareType;
    public String spareStatus;
    public String price;
    public Integer quantity;
    public String updatedAt;
}
