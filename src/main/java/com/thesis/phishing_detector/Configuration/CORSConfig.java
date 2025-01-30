package com.thesis.phishing_detector.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/check-url") // Apply to all endpoints
                .allowedOrigins("https://mail.google.com","http://127.0.0.1:5500") // Allow specific origins
                .allowedMethods("POST","GET") // Allow specific HTTP methods
                .allowedHeaders("Client-Type", "Content-Type") // Allow all headers
                .maxAge(3600); // Max age for preflight requests (in seconds)
    }

}
