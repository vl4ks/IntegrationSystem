package org.denisova.integrationapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SpareProcessingRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:processSpare")
                .routeId("process-spare")


                .setProperty("spareDto", body())
                .setHeader("syncRunId", header("syncRunId"))

                // Проверка существование записи
                .setHeader("spareCode", simple("${exchangeProperty.spareDto.spareCode}"))
                .to("sql:SELECT id FROM spare_parts WHERE spare_code = :#spareCode?dataSource=#dataSource")

                .choice()
                .when(simple("${body.size} == 0"))
                // Новая запись
                .setBody(simple("${exchangeProperty.spareDto}"))
                .to("sql:INSERT INTO spare_parts " +
                        "(spare_code, name, description, type, status, price, quantity, updated_at, is_active, last_seen_at) " +
                        "VALUES (:#${body.spareCode}, :#${body.spareName}, " +
                        ":#${body.spareDescription}, :#${body.spareType}, " +
                        ":#${body.spareStatus}, :#${body.price}, " +
                        ":#${body.quantity}, :#${body.updatedAt}, " +
                        "true, CURRENT_TIMESTAMP)?dataSource=#dataSource")

                // Создание версионной записи
                .to("sql:INSERT INTO spare_part_versions " +
                        "(spare_code, name, description, type, status, price, quantity, updated_at, change_kind) " +
                        "VALUES (:#${body.spareCode}, :#${body.spareName}, " +
                        ":#${body.spareDescription}, :#${body.spareType}, " +
                        ":#${body.spareStatus}, :#${body.price}, " +
                        ":#${body.quantity}, :#${body.updatedAt}, 'CREATED')?dataSource=#dataSource")

                .setProperty("changeType", constant("INSERTED"))
                .otherwise()
                .setBody(simple("${exchangeProperty.spareDto}"))
                .to("sql:UPDATE spare_parts SET " +
                        "name = :#${body.spareName}, " +
                        "description = :#${body.spareDescription}, " +
                        "type = :#${body.spareType}, " +
                        "status = :#${body.spareStatus}, " +
                        "price = :#${body.price}, " +
                        "quantity = :#${body.quantity}, " +
                        "updated_at = :#${body.updatedAt}, " +
                        "is_active = true, " +
                        "last_seen_at = CURRENT_TIMESTAMP " +
                        "WHERE spare_code = :#${body.spareCode}?dataSource=#dataSource")

                .to("sql:INSERT INTO spare_part_versions " +
                        "(spare_code, name, description, type, status, price, quantity, updated_at, change_kind) " +
                        "VALUES (:#${body.spareCode}, :#${body.spareName}, " +
                        ":#${body.spareDescription}, :#${body.spareType}, " +
                        ":#${body.spareStatus}, :#${body.price}, " +
                        ":#${body.quantity}, :#${body.updatedAt}, 'UPDATED')?dataSource=#dataSource")

                .setProperty("changeType", constant("UPDATED"))
                .end()

                // Отправка события статистики
                .to("direct:collectStats");
    }
}
