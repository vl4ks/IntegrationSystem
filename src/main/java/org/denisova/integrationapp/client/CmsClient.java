package org.denisova.integrationapp.client;

import org.denisova.integrationapp.client.dto.CmsSpareDto;
import org.denisova.integrationapp.client.dto.CmsSpareRawDto;
import org.denisova.integrationapp.config.AppProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Клиент для обращения к CMS API.
 * Выполняет GET-запросы (с пагинацией) и возвращает список деталей.
 */
@Component
public class CmsClient {
    private static final ParameterizedTypeReference<List<CmsSpareDto>> LIST_OF_SPARES =
            new ParameterizedTypeReference<>() {};

    private static final ParameterizedTypeReference<java.util.List<CmsSpareRawDto>> LIST_OF_RAW =
            new ParameterizedTypeReference<>() {};

    private final WebClient webClient;
    private final AppProperties props;

    public CmsClient(WebClient webClient, AppProperties props) {
        this.webClient = webClient;
        this.props = props;
    }

    /**
     * Загружает одну страницу деталей из CMS.
     * API возвращает корневой JSON-массив.
     */
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

    /**
     * Загружает «сырые» данные (все поля строки) для построения точного CSV.
     */
    public java.util.List<CmsSpareRawDto> getSparesPageRaw(int page, int size) {
        String url = props.getCms().getBaseUrl()
                + "/students/" + props.getStudentId()
                + "/cms/spares?page=" + page + "&size=" + size;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(LIST_OF_RAW)
                .block();
    }
}
