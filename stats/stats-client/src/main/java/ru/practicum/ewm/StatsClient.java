package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatsClient {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final WebClient webClient;

    public StatsClient(@Value("${client.url:http://localhost:9090}") String url) {
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
        return webClient
                .post()
                .uri("/hit")
                .body(Mono.just(endpointHitDto), EndpointHitDto.class)
                .retrieve()
                .bodyToMono(EndpointHitDto.class)
                .block();
    }

    public ResponseEntity<List<ViewStats>> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String startDate = start.format(dateTimeFormatter);
        String endDate = end.format(dateTimeFormatter);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", startDate)
                        .queryParam("end", endDate)
                        .queryParam("uris", String.join(",", uris))
                        .queryParam("unique", unique.toString())
                        .build())
                .retrieve()
                .toEntityList(ViewStats.class)
                .block();
    }
}
