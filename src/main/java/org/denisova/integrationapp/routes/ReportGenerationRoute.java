package org.denisova.integrationapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.springframework.stereotype.Component;

@Component
public class ReportGenerationRoute extends RouteBuilder {
    @Override
    public void configure() {
        CsvDataFormat csv = new CsvDataFormat();
        csv.setDelimiter(';');
        csv.setSkipHeaderRecord(true);

        // Генерация отчета по таймеру каждый час
        from("timer:reportGen?period=3600000")
                .routeId("report-generation")
                .log("Начало генерации отчета")

                // Получаем данные из БД
                .to("sql:SELECT spare_code, name, description, type, status, price, quantity, updated_at " +
                        "FROM spare_parts ORDER BY spare_code?dataSource=#dataSource")

                // Преобразуем в CSV
                .marshal(csv)
                .convertBodyTo(String.class)

                // Сохраняем во временный файл
                .to("file:reports?fileName=temp-report-${date:now:yyyyMMddHHmmss}.csv")

                // Отправляем в Report API
                .setHeader("CamelHttpMethod", constant("POST"))
                .setHeader("Content-Type", constant("text/csv; charset=UTF-8"))
                .toD("${properties:app.report.base-url}/students/${properties:app.student-id}/report/csv")

                .to("file:reports/responses?fileName=response-${date:now:yyyyMMddHHmmss}.txt")

                .log("Отчет успешно отправлен");

    }
}
