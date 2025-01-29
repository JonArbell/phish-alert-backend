package com.thesis.phishing_detector.Services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UrlProcessingService {

    private final Logger logger = LoggerFactory.getLogger(UrlProcessingService.class);

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

                logger.info("Arduino Response :  {}",arduinoResponse);
                return openAiResponse;
            }

//            var arduinoResponse = arduinoService.sendResponse(googleResponse);
//
//            logger.info("Arduino Response :  {}",arduinoResponse);

            return googleResponse;

        }catch (RuntimeException e){

            logger.info("Process Exception Error : {}",e.getMessage());

            return e.getMessage();
        }
    }

}
