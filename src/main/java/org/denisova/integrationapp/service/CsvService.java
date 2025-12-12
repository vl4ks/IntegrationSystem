package org.denisova.integrationapp.service;

import org.denisova.integrationapp.repo.SparePartRepository;
import org.springframework.stereotype.Service;

/**
 * Сервис для генерации CSV из данных БД.
 * Используется при отладке и анализе локальных данных.
 */
@Service
public class CsvService {
    private final SparePartRepository spareRepo;

    public CsvService(SparePartRepository spareRepo) {
        this.spareRepo = spareRepo;
    }

    /**
     * Формирует CSV по записям в таблице spare_parts
     */
    public String buildCsv() {
        var items = spareRepo.findAll();
        var sb = new StringBuilder(4096);

        for (var sp : items) {
            sb.append(nullToEmpty(sp.getSpareCode())).append(';')
                    .append(nullToEmpty(sp.getName())).append(';')
                    .append(escapeSemi(nullToEmpty(sp.getDescription()))).append(';')
                    .append(nullToEmpty(sp.getType())).append(';')
                    .append(nullToEmpty(sp.getStatus())).append(';')
                    .append(sp.getPrice() == null ? "" : sp.getPrice()).append(';')
                    .append(sp.getQuantity() == null ? "" : sp.getQuantity()).append(';')
                    .append(nullToEmpty(sp.getUpdatedAt()))
                    .append('\n');
        }
        return sb.toString();
    }

    private static String nullToEmpty(Object o) { return o == null ? "" : o.toString(); }
    private static String escapeSemi(String s) {
        return s.replace("\n", " ").replace("\r", " ");
    }
}
