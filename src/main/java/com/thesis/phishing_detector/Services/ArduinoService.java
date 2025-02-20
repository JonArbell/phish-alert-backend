package com.thesis.phishing_detector.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class ArduinoService {

    private final WebClient webClient;

    public ArduinoService(WebClient.Builder webClient) {

        var ipAddress = "";

        try{

            ipAddress = InetAddress.getByName("arduino.local").getHostAddress();

        } catch (UnknownHostException e) {

            log.error(e.getClass().getName(),e.getMessage());

        }

        log.info("Ip Address : {}",ipAddress);

        this.webClient = webClient.baseUrl(ipAddress).build();
    }

    public String sendResponse(String apiResponse){

        var requestBody = Map.of("send",apiResponse);

        return webClient.post()
                .uri("/send-response")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .doOnNext(response -> log.info("From arduino response : {}", response))
                .onErrorResume(error -> {
                    log.error("Arduino Api Unexpected error: {}", error.getMessage());
                    return Mono.just("An unexpected error occurred.");
                })
                .block();
    }

}
