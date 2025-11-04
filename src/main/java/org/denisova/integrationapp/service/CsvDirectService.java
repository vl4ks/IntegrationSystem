package org.denisova.integrationapp.service;

import org.apache.camel.Header;
import org.denisova.integrationapp.client.CmsClient;
import org.denisova.integrationapp.client.dto.CmsSpareRawDto;
import org.denisova.integrationapp.config.AppProperties;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CsvDirectService {
    private final CmsClient cmsClient;
    private final AppProperties props;

    public CsvDirectService(CmsClient cmsClient, AppProperties props) {
        this.cmsClient = cmsClient;
        this.props = props;
    }

    /**
     * Генерирует CSV напрямую из CMS, сохраняя точный формат значений
     * (без заголовка, разделитель ';', UTF-8).
     */
    public String buildCsvFromCms(@Header("onlyActive") Boolean onlyActive) {
        boolean onlyAct = Boolean.TRUE.equals(onlyActive);
        int page = 0;
        int size = props.getCms().getPageSize();
        StringBuilder sb = new StringBuilder(8192);

        while (true) {
            List<CmsSpareRawDto> content = cmsClient.getSparesPageRaw(page, size);
            if (content == null || content.isEmpty()) break;

            for (CmsSpareRawDto sp : content) {
                sb.append(n(sp.spareCode)).append(';')
                        .append(n(sp.spareName)).append(';')
                        .append(clean(n(sp.spareDescription))).append(';')
                        .append(n(sp.spareType)).append(';')
                        .append(n(sp.spareStatus)).append(';')
                        .append(n(sp.price)).append(';')
                        .append(sp.quantity == null ? "" : sp.quantity).append(';')
                        .append(n(sp.updatedAt))
                        .append('\n');
            }
            page++;
        }
        return sb.toString();
    }

    private static String n(String s) { return s == null ? "" : s; }
    private static String clean(String s) { return s.replace("\n"," ")
            .replace("\r"," "); }

}
