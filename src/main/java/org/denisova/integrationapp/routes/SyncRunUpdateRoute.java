package org.denisova.integrationapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Финальный этап прогона синхронизации: закрываем sync_run и добираем статистику.
 */
@Component
public class SyncRunUpdateRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:updateSyncStats")
                .routeId("update-sync-stats")

                .setHeader("runFinished", constant(true))
                .to("direct:collectStats")

                .to("sql:UPDATE sync_run SET finished_at = CURRENT_TIMESTAMP, status = 'OK' WHERE id = :#${header.syncRunId}?dataSource=#dataSource");
    }
}

