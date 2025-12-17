package org.denisova.integrationapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DeactivationRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:deactivateMissing")
                .routeId("deactivate-missing")
                .setHeader("syncRunId", header("syncRunId"))

                // Находим записи, которые не обновлялись в текущей синхронизации
                .to("sql:SELECT * FROM spare_parts WHERE " +
                        "(last_seen_at < (SELECT started_at FROM sync_run WHERE id = :#${header.syncRunId}) " +
                        "OR last_seen_at IS NULL) AND is_active = true?dataSource=#dataSource")

                .split(body())
                .to("sql:UPDATE spare_parts SET is_active = false WHERE id = :#${body[id]}?dataSource=#dataSource")

                .to("sql:INSERT INTO spare_part_versions " +
                        "(spare_code, name, description, type, status, price, quantity, updated_at, change_kind) " +
                        "VALUES (:#${body[spare_code]}, :#${body[name]}, " +
                        ":#${body[description]}, :#${body[type]}, " +
                        ":#${body[status]}, :#${body[price]}, " +
                        ":#${body[quantity]}, :#${body[updated_at]}, 'DEACTIVATED')?dataSource=#dataSource")

                .setProperty("changeType", constant("DEACTIVATED"))
                .to("direct:collectStats")
                .end();
    }
}
