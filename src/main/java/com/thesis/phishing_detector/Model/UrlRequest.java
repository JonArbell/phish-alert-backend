package com.thesis.phishing_detector.Model;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class UrlRequest {
    @URL(message = "Please provide a valid URL.")
    @NotBlank(message = "URL is required.")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UrlRequest{" +
                "url='" + url + '\'' +
                '}';
    }

    public UrlRequest(String url) {
        this.url = url;
    }

    public UrlRequest() {
    }
}
