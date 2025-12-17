package org.denisova.integrationapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.denisova.integrationapp.dto.CmsSpareDto;
import org.springframework.stereotype.Component;


@Component
public class CmsSyncRoute extends RouteBuilder {
    @Override
    public void configure() {
        // Маршрут синхронизации по таймеру каждые 5 минут
        from("timer:cmsSync?period=300000")
                .routeId("cms-sync-timer")
                .log("Запуск синхронизации CMS → БД")

                // Запись синхронизации
                .to("sql:INSERT INTO sync_run (started_at, status) VALUES (CURRENT_TIMESTAMP, 'STARTED') RETURNING id?dataSource=#dataSource")
                .setHeader("syncRunId", simple("${body[0][id]}"))

                .setProperty("page", constant(0))
                .setProperty("totalProcessed", constant(0))
                .setProperty("continueLoop", constant(true))

                .loopDoWhile(simple("${exchangeProperty.continueLoop} == true"))
                .setHeader("CamelHttpMethod", constant("GET"))
                .toD("${properties:app.cms.base-url}/students/${properties:app.student-id}"
                        + "/cms/spares?page=${exchangeProperty.page}&size=${properties:app.cms.page-size}")

                .choice()
                .when(simple("${header.CamelHttpResponseCode} != 200"))
                .log("Ошибка HTTP ${header.CamelHttpResponseCode}")
                .setProperty("continueLoop", constant(false))
                .otherwise()
                .unmarshal().json(JsonLibrary.Jackson, CmsSpareDto[].class)

                .process(exchange -> {
                    CmsSpareDto[] data = exchange.getIn().getBody(CmsSpareDto[].class);
                    if (data == null || data.length == 0) {
                        exchange.setProperty("continueLoop", false);
                    } else {
                        exchange.setProperty("page", exchange.getProperty("page", Integer.class) + 1);
                        exchange.setProperty("currentBatch", data);
                    }
                })

                .filter(simple("${exchangeProperty.currentBatch} != null"))
                // Разделяем массив на отдельные записи
                .split(simple("${exchangeProperty.currentBatch}"))
                .to("direct:processSpare")
                .end()
                .end()
                .endChoice()
                .end()

                // Деактивация отсутствующих записей
                .to("direct:deactivateMissing")

                // Обновление статистики синхронизации
                .to("direct:updateSyncStats")

                .log("Синхронизация завершена успешно");
    }
}
