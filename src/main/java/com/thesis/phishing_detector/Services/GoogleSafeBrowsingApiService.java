package com.thesis.phishing_detector.Services;

import com.thesis.phishing_detector.Model.GoogleSafeApiModel.Client;
import com.thesis.phishing_detector.Model.GoogleSafeApiModel.GoogleSafeRequest;
import com.thesis.phishing_detector.Model.GoogleSafeApiModel.ThreatInfo;
import com.thesis.phishing_detector.Model.GoogleSafeApiModel.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Arrays;
import java.util.List;

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

            var client = new Client("phish-alert","1.1");

            var threatInfo = ThreatInfo
                        .builder()
                        .threatTypes(threatTypes)
                        .platformTypes(List.of("ANY_PLATFORM"))
                        .threatEntryTypes(List.of("URL"))
                        .threatEntries(List.of(new URL(url)))
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
                    .doOnNext(response -> {
                        logger.info("Google Safe Api response : {}", response);

                        // If the response contains matches, there are threats
//                    if (response.contains("matches")) {
//                        logger.warn("URL is malicious: {}", url);
//                    } else {
//                        logger.info("URL is safe: {}", url);
//                    }
                    })
                    .doOnError(error -> {
                        logger.error("Google Safe Api Error : {}", error.getMessage());
                        throw new RuntimeException(error.getMessage());
                    })
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
