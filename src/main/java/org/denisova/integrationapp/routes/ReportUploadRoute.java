package org.denisova.integrationapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel-маршрут для формирования и отправки CSV-отчёта.
 * Использует CsvService и ReportClient.
 */
@Component
public class ReportUploadRoute extends RouteBuilder {
    @Override
    public void configure() {
        // Формирование CSV и отправка в Report API
        from("direct:upload-report")
                .routeId("upload-report")
                .bean("csvService", "buildCsv")
                .bean("reportClient", "uploadCsv");
    }
}
