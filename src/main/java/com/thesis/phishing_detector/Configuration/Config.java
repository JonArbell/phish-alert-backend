package com.thesis.phishing_detector.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.concurrent.TimeUnit;

public class Config {

    @Bean
    public WebClient webclientBuilder(){
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    @Bean
    public CacheManager cacheManager() {
        var cacheManager = new SimpleCacheManager();

        var caffeineCache = new CaffeineCache("urls",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(1000)
                        .build());

        cacheManager.setCaches(java.util.List.of(caffeineCache));
        return cacheManager;
    }

}
