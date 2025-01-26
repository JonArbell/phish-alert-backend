package com.thesis.phishing_detector.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoogleSafeBrowsingApiService implements ApiService{
    @Value("${google.safebrowsing.api.key}")
    private String apiKey;

    @Value("${google.safebrowsing.api.url}")
    private String uri;

    private final Logger logger = LoggerFactory.getLogger(GoogleSafeBrowsingApiService.class);

    private final WebClient webClient;

    public GoogleSafeBrowsingApiService(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("https://safebrowsing.googleapis.com").build();
    }

    @Override
    public String analyzeUrl(String url){
        var threatTypes = Arrays.asList("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE",
                "POTENTIALLY_HARMFUL_APPLICATION");

        try {
            // Request Payload
            var requestBody = new HashMap<>();

            requestBody.put("client", Map.of("clientId", "phish-alert", "clientVersion", "1.1"));
            requestBody.put("threatInfo", Map.of(
                    "threatTypes", threatTypes,
                    "platformTypes", List.of("ANY_PLATFORM"),
                    "threatEntryTypes", List.of("URL"),
                    "threatEntries", List.of(Map.of("url", url))
            ));

            return webClient.post()
                    .uri(uri + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> {
                        logger.info("Google Safe Api response : {}", response);

                        // If the response contains matches, there are threats
//                    if (response.contains("matches")) {
//                        logger.warn("URL is malicious: {}", url);
//                    } else {
//                        logger.info("URL is safe: {}", url);
//                    }
                    })
                    .doOnError(error -> logger.error("Google Safe Api Error : {}", error.getMessage()))
//                .map(response -> {
//                    if (response.contains("matches")){
//                        return "Suspicious";
//                    }
//                    return "Safe";
//                })
                    .block();

        } catch (Exception e) {
            logger.error("Error occurred: {}", e.getMessage());
            return e.getMessage();
        }
    }


}
