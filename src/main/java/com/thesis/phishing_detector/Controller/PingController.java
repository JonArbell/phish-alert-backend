package com.thesis.phishing_detector.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.Map;

@Controller
public class PingController {

    private final Logger logger = LoggerFactory.getLogger(PingController.class);

    private final WebClient webClient;

    public PingController(WebClient.Builder webClient){
        this.webClient = webClient.baseUrl("").build();
    }

    @GetMapping("/api/ping")
    public Map<String, String> getPing(){
        return Map.of("Ping","Tamang ping lang");
    }

    @Async
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void ping() {

        webClient.post()
                .uri("/api/ping")
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(
                        result -> logger.info("Ping successful, Result: {}", result),
                        error -> {
                            if (error instanceof WebClientResponseException webClientException) {
                                logger.error("Ping failed with status {}: {}", webClientException.getStatusCode(), webClientException.getMessage());
                            } else {
                                logger.error("Exception Error: {}", error.getMessage());
                            }
                        }
                );
    }

}
