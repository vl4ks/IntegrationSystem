package org.denisova.integrationapp.service;

import org.apache.camel.Header;
import org.denisova.integrationapp.repo.SparePartRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Сервис для генерации CSV из данных БД.
 * Используется при отладке и анализе локальных данных.
 */
@Service
public class CsvService {
    private final SparePartRepository spareRepo;
    private static final DateTimeFormatter ISO_LOCAL = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public CsvService(SparePartRepository spareRepo) {
        this.spareRepo = spareRepo;
    }

    /**
     * Формирует CSV по записям в таблице spare_parts.
     * Может фильтровать только активные записи.
     */
    public String buildCsv(@Header("onlyActive") boolean onlyActive) {
        var items = spareRepo.findAll();
        var sb = new StringBuilder(4096);

        for (var sp : items) {
            if (onlyActive && !Boolean.TRUE.equals(sp.getIsActive())) continue;

            sb.append(nullToEmpty(sp.getSpareCode())).append(';')
                    .append(nullToEmpty(sp.getName())).append(';')
                    .append(escapeSemi(nullToEmpty(sp.getDescription()))).append(';')
                    .append(nullToEmpty(sp.getType())).append(';')
                    .append(nullToEmpty(sp.getStatus())).append(';')
                    .append(sp.getPrice() == null ? "" : sp.getPrice()).append(';')
                    .append(sp.getQuantity() == null ? "" : sp.getQuantity()).append(';')
                    .append(formatLocal(sp.getUpdatedAt()))
                    .append('\n');
        }
        return sb.toString();
    }

    private static String formatLocal(Object ts) {
        if (ts == null) return "";
        if (ts instanceof OffsetDateTime odt) {
            return ISO_LOCAL.format(odt.toLocalDateTime());
        }
        if (ts instanceof java.time.LocalDateTime ldt) {
            return ISO_LOCAL.format(ldt);
        }
        return ts.toString();
    }

    private static String nullToEmpty(Object o) { return o == null ? "" : o.toString(); }
    private static String escapeSemi(String s) {
        return s.replace("\n", " ").replace("\r", " ");
    }
}
