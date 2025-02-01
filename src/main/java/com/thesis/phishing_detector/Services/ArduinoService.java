package com.thesis.phishing_detector.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.net.InetAddress;

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
        this.webClient = webClient.baseUrl(ipAddress).build();
    }

    public String sendResponse(String response){

        try {
            // Request Payload
            var requestBody = new HashMap<>();

            requestBody.put("send",response);

            return webClient.post()
                    .uri("/send-response")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(arduinoResponse -> log.info("Arduino response : {}", arduinoResponse))
                    .doOnError(error -> {
                        log.error("Arduino Error : {}", error.getMessage());
                        throw new RuntimeException(error.getMessage());
                    })
                    .block();

        } catch (Exception e) {
            log.error("Arduino error occurred: {}", e.getMessage());
            return e.getMessage();
        }
    }

}
