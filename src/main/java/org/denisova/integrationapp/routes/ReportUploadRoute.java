package org.denisova.integrationapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel-маршрут для формирования и отправки CSV-отчёта.
 * Использует CsvDirectService и ReportClient.
 */
@Component
public class ReportUploadRoute extends RouteBuilder {
    @Override
    public void configure() {
        // Формирование CSV напрямую из CMS и отправка в Report API
        from("direct:upload-report")
                .routeId("upload-report")
                .bean("csvDirectService", "buildCsvFromCms(${header.onlyActive})")
                .bean("reportClient", "uploadCsv");
    }
}
