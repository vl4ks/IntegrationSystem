package org.denisova.integrationapp.client;

import org.denisova.integrationapp.config.AppProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Клиент для Report API: загрузка CSV и запрос результата проверки.
 */
@Component
public class ReportClient {
    private final WebClient webClient;
    private final AppProperties props;

    public ReportClient(WebClient webClient, AppProperties props) {
        this.webClient = webClient;
        this.props = props;
    }

    /**
     * Отправляет CSV в проверяющую систему.
     * @return тело ответа - строка
     */
    public String uploadCsv(String csvUtf8) {
        String url = props.getReport().getBaseUrl()
                + "/students/" + props.getStudentId()
                + "/report/csv";

        return webClient.post().uri(url)
                .contentType(MediaType.valueOf("text/csv; charset=UTF-8"))
                .bodyValue(csvUtf8)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(ex -> Mono.error(new RuntimeException("Report upload failed: " + ex.getMessage(), ex)))
                .block();
    }

    /**
     * Возвращает последний результат проверки CSV.
     */
    public String getResults() {
        String url = props.getReport().getBaseUrl()
                + "/students/" + props.getStudentId()
                + "/results";

        return webClient.get().uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
