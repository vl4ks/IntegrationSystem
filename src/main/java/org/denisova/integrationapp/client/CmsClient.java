package org.denisova.integrationapp.client;

import org.denisova.integrationapp.client.dto.CmsSpareDto;
import org.denisova.integrationapp.config.AppProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CmsClient {
    private static final ParameterizedTypeReference<List<CmsSpareDto>> LIST_OF_SPARES =
            new ParameterizedTypeReference<>() {};

    private final WebClient webClient;
    private final AppProperties props;

    public CmsClient(WebClient webClient, AppProperties props) {
        this.webClient = webClient;
        this.props = props;
    }

    public List<CmsSpareDto> getSparesPage(int page, int size) {
        String url = props.getCms().getBaseUrl()
                + "/students/" + props.getStudentId()
                + "/cms/spares?page=" + page + "&size=" + size;

        return webClient.get().uri(url)
                .retrieve()
                .bodyToMono(LIST_OF_SPARES)
                .onErrorResume(ex -> Mono.error(new RuntimeException("CMS call failed: " + ex.getMessage(), ex)))
                .block();
    }
}
