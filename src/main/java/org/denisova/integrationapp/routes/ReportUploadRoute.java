package org.denisova.integrationapp.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ReportUploadRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:upload-report")
                .routeId("upload-report")
                .bean("csvService", "buildCsv(${header.onlyActive})")
                .bean("reportClient", "uploadCsv");
    }
}
