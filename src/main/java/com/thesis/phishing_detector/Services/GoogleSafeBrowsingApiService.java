package com.thesis.phishing_detector.Services;

import com.thesis.phishing_detector.Model.GoogleSafeApiModel.Client;
import com.thesis.phishing_detector.Model.GoogleSafeApiModel.GoogleSafeRequest;
import com.thesis.phishing_detector.Model.GoogleSafeApiModel.ThreatInfo;
import com.thesis.phishing_detector.Model.UrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class GoogleSafeBrowsingApiService implements ApiService{
    @Value("${google.safebrowsing.api.key}")
    private String apiKey;

    @Value("${google.safebrowsing.api.url}")
    private String uri;

    private final WebClient webClient;

    public GoogleSafeBrowsingApiService(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("https://safebrowsing.googleapis.com").build();
    }

    @Override
    public String analyzeUrl(String url){
        var threatTypes = Arrays.asList("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE",
                "POTENTIALLY_HARMFUL_APPLICATION");

        var client = new Client("phish-alert","1.1");

        var threatInfo = ThreatInfo
                .builder()
                .threatTypes(threatTypes)
                .platformTypes(List.of("ANY_PLATFORM"))
                .threatEntryTypes(List.of("URL"))
                .threatEntries(List.of(new UrlRequest(url)))
                .build();

        var requestBody = GoogleSafeRequest
                .builder()
                .client(client)
                .threatInfo(threatInfo)
                .build();

        return webClient.post()
                .uri(uri + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .doOnNext(response -> log.info("Google Safe Api response : {}", response))
                .map(response -> response.contains("matches") ? "Suspicious" : "Safe")
                .onErrorResume(error -> {
                    log.error("Google Safe Api Unexpected error: {}", error.getMessage());
                    return Mono.just("An unexpected error occurred.");
                })
                .block();
    }

}
