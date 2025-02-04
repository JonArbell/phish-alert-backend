package com.thesis.phishing_detector.Services;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Service
@Setter
public class UrlProcessingService {



    private final ApiService googleSafeBrowsingApiService;

    private final ApiService openAiService;

    private final ArduinoService arduinoService;

    private final Executor executor = Executors.newFixedThreadPool(5);

    public String processUrl(String url){

        var apiResponse = responseOfApis(url);

        log.info("Api Response : {}",apiResponse);

        CompletableFuture.runAsync(() -> {
            var arduinoResponse = arduinoService.sendResponse(apiResponse);
            log.info("Arduino Response : {}", arduinoResponse);
        },executor);

        return apiResponse;

    }

    @Cacheable(value = "urls", key = "#url")
    public String responseOfApis(String url){

        try{

            var googleResponse = googleSafeBrowsingApiService.analyzeUrl(url);

            if("Safe".equals(googleResponse))
                return openAiService.analyzeUrl(url);

            return googleResponse;

        }catch (RuntimeException e){

            log.error("Process Exception Error : {}",e.getMessage());

            return e.getMessage();
        }
    }

}
