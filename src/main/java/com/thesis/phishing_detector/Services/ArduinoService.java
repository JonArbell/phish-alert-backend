package com.thesis.phishing_detector.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.HashMap;

@Service
public class ArduinoService {
    private final WebClient webClient;

    private final Logger logger = LoggerFactory.getLogger(ArduinoService.class);

    public ArduinoService(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("192.168.1.3").build();
    }

    public String sendResponse(String response){

        try {
            // Request Payload
            var requestBody = new HashMap<>();

            requestBody.put("response",response);

            return webClient.post()
                    .uri("/send-response")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(arduinoResponse -> logger.info("Arduino response : {}", arduinoResponse))
                    .doOnError(error -> {
                        logger.error("Arduino Error : {}", error.getMessage());
                        throw new RuntimeException(error.getMessage());
                    })
                    .block();

        } catch (Exception e) {
            logger.error("Arduino error occurred: {}", e.getMessage());
            return e.getMessage();
        }
    }

}
