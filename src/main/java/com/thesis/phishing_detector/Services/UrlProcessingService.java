package com.thesis.phishing_detector.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UrlProcessingService {

    private final Logger logger = LoggerFactory.getLogger(UrlProcessingService.class);

    private final ApiService googleSafeBrowsingApiService;

    private final ApiService openAiService;

    public UrlProcessingService(GoogleSafeBrowsingApiService googleSafeBrowsingApiService, OpenAiService openAiService) {
        this.googleSafeBrowsingApiService = googleSafeBrowsingApiService;
        this.openAiService = openAiService;
    }

    @Cacheable(value = "urls", key = "#url")
    public String processUrl(String url){

        try{
            var googleResponse = googleSafeBrowsingApiService.analyzeUrl(url);

            if("{}\n".equals(googleResponse))
                return openAiService.analyzeUrl(url);

            return googleResponse;

        }catch (RuntimeException e){

            logger.info("Process Exception Error : {}",e.getMessage());

            return e.getMessage();
        }
    }

}
