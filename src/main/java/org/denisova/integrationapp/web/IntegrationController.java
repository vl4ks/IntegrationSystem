package org.denisova.integrationapp.web;

import org.apache.camel.ProducerTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/integration")
public class IntegrationController {
    private final ProducerTemplate template;

    public IntegrationController(ProducerTemplate template) {
        this.template = template;
    }

    @PostMapping("/run-sync")
    public ResponseEntity<?> runSync() {
        var result = template.requestBody("direct:sync-cms", (Object) null);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/upload-report")
    public ResponseEntity<?> uploadReport(@RequestParam(name = "onlyActive", defaultValue = "true") boolean onlyActive) {
        var result = template.requestBodyAndHeader("direct:upload-report", null, "onlyActive", onlyActive);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/results")
    public ResponseEntity<?> getResults() {
        var result = template.requestBody("bean:reportClient?method=getResults", (Object) null);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/preview-csv", produces = "text/csv; charset=UTF-8")
    public ResponseEntity<String> previewCsv(
            @RequestParam(name = "onlyActive", defaultValue = "true") boolean onlyActive) {

        String csv = template.requestBodyAndHeader(
                "bean:csvService?method=buildCsv",
                (Object) null,
                "onlyActive",
                onlyActive,
                String.class
        );
        return ResponseEntity.ok(csv);
    }

}
