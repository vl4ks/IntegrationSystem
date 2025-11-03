package org.denisova.integrationapp.service;

import org.denisova.integrationapp.domain.SparePart;
import org.denisova.integrationapp.repo.SparePartRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CsvService {
    private final SparePartRepository spareRepo;
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public CsvService(SparePartRepository spareRepo) {
        this.spareRepo = spareRepo;
    }

    public String buildCsv(boolean onlyActive) {
        List<SparePart> items = spareRepo.findAll();

        StringBuilder sb = new StringBuilder(4096);
        for (SparePart sp : items) {
            if (onlyActive && !Boolean.TRUE.equals(sp.getIsActive())) continue;

            // порядок строго по заданию:
            // spareCode;spareName;spareDescription;spareType;spareStatus;price;quantity;updatedAt
            sb.append(nullToEmpty(sp.getSpareCode())).append(';')
                    .append(nullToEmpty(sp.getName())).append(';')
                    .append(escapeSemi(nullToEmpty(sp.getDescription()))).append(';')
                    .append(nullToEmpty(sp.getType())).append(';')
                    .append(nullToEmpty(sp.getStatus())).append(';')
                    .append(sp.getPrice() == null ? "" : sp.getPrice()).append(';')
                    .append(sp.getQuantity() == null ? "" : sp.getQuantity()).append(';')
                    .append(sp.getUpdatedAt() == null ? "" : ISO.format(sp.getUpdatedAt()))
                    .append('\n');
        }
        // ВАЖНО: без заголовка; кодировка UTF-8 — обеспечивается на клиенте при отправке.
        return new String(sb.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    private static String nullToEmpty(Object o) { return o == null ? "" : o.toString(); }
    private static String escapeSemi(String s) {
        // если надо — можно экранировать ; кавычками, но задание не требует
        return s.replace("\n", " ").replace("\r", " ");
    }
}
