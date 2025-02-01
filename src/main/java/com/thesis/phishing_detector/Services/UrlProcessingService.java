package com.thesis.phishing_detector.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlProcessingService {

    private final ApiService googleSafeBrowsingApiService;

    private final ApiService openAiService;

    private final ArduinoService arduinoService;

    @Cacheable(value = "urls", key = "#url")
    public String processUrl(String url){

        try{

            var googleResponse = googleSafeBrowsingApiService.analyzeUrl(url);

            if("{}\n".equals(googleResponse)){

                var openAiResponse = openAiService.analyzeUrl(url);

                var arduinoResponse = arduinoService.sendResponse(openAiResponse);

                log.info("Arduino Response :  {}",arduinoResponse);

                return openAiResponse;
            }

            var arduinoResponse = arduinoService.sendResponse(googleResponse);

            log.info("Arduino Response :  {}",arduinoResponse);

            return googleResponse;

        }catch (RuntimeException e){

            log.info("Process Exception Error : {}",e.getMessage());

            return e.getMessage();
        }
    }


}
