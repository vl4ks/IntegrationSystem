package org.denisova.integrationapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.denisova.integrationapp.aggregation.StatisticAggregationStrategy;
import org.springframework.stereotype.Component;

/**
 * Маршрут агрегации статистики синхронизации.
 * Принимает события с признаком changeType и суммирует по syncRunId.
 */
@Component
public class StatsRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:collectStats")
                .routeId("collect-stats")

                .aggregate(header("syncRunId"), new StatisticAggregationStrategy())
                .completionInterval(2000)
                .completionPredicate(simple("${header.runFinished} == true"))

                .setHeader("inserted", simple("${body.inserted}"))
                .setHeader("updated", simple("${body.updated}"))
                .setHeader("deactivated", simple("${body.deactivated}"))
                .setHeader("totalProcessed", simple("${body.totalProcessed}"))

                .to("sql:UPDATE sync_run SET " +
                        "inserted = :#inserted, " +
                        "updated = :#updated, " +
                        "deactivated = :#deactivated, " +
                        "total_from_cms = :#totalProcessed " +
                        "WHERE id = :#syncRunId?dataSource=#dataSource");
    }
}

