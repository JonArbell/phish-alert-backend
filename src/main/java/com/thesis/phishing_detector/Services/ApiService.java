package com.thesis.phishing_detector.Services;


import reactor.core.publisher.Mono;

public interface ApiService {
    String analyzeUrl(String url);
}
