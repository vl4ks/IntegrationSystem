package org.denisova.integrationapp.web;

import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления интеграцией.
 * Позволяет запускать синхронизацию, выгрузку отчёта и предварительный просмотр CSV.
 */
@RestController
@RequestMapping("/integration")
public class IntegrationController {
    private final ProducerTemplate template;

    public IntegrationController(ProducerTemplate template) {
        this.template = template;
    }

    /**
     * Запускает процесс синхронизации данных CMS → БД.
     */
    @PostMapping("/run-sync")
    public ResponseEntity<?> runSync() {
        var result = template.requestBody("direct:sync-cms", (Object) null);
        return ResponseEntity.ok(result);
    }

    /**
     * Формирует CSV и отправляет в проверяющую систему.
     */
    @PostMapping("/upload-report")
    public ResponseEntity<?> uploadReport() {
        var result = template.requestBody("direct:upload-report", (Object) null);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/results")
    public ResponseEntity<?> getResults() {
        var result = template.requestBody("bean:reportClient?method=getResults", (Object) null);
        return ResponseEntity.ok(result);
    }

    /**
     * Возвращает CSV как текст для предварительного просмотра.
     */
    @GetMapping(value = "/preview-csv", produces = "text/csv; charset=UTF-8")
    public ResponseEntity<String> previewCsv() {

        String csv = template.requestBody(
                "bean:csvService?method=buildCsv",
                (Object) null,
                String.class
        );
        return ResponseEntity.ok(csv);
    }

}
