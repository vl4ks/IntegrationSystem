package org.denisova.integrationapp.client;

import org.denisova.integrationapp.config.AppProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ReportClient {
    private final WebClient webClient;
    private final AppProperties props;

    public ReportClient(WebClient webClient, AppProperties props) {
        this.webClient = webClient;
        this.props = props;
    }

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
