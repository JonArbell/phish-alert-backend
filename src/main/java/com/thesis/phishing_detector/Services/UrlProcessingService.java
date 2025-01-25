package com.thesis.phishing_detector.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.net.URL;

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

        if(isValidUrl(url)){

            var googleResponse = googleSafeBrowsingApiService.analyzeUrl(url);

            if(googleResponse.equals("{}\n"))
                return openAiService.analyzeUrl(url);

            return googleResponse;
        }

        return "URL is not valid. Please enter valid URL.";

    }

    private boolean isValidUrl(String url) {
        try {
            var parsedUrl = new URL(url);
            String protocol = parsedUrl.getProtocol();
            return protocol.equals("http") || protocol.equals("https");
        } catch (Exception e) {
            return false;
        }
    }


}
