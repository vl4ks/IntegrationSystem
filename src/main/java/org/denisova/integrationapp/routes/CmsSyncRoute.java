package org.denisova.integrationapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel-маршрут для запуска синхронизации CMS → БД.
 * Вызывается из REST-контроллера: POST /integration/run-sync
 */
@Component
public class CmsSyncRoute extends RouteBuilder {
    @Override
    public void configure() {
        // on-demand маршрут: дергаем из REST-контроллера
        from("direct:sync-cms")
                .routeId("sync-cms")
                .bean("syncService", "syncAll");
    }
}
