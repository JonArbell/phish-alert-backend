package com.thesis.phishing_detector.Model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlRequest {

    @URL(message = "Please provide a valid URL.")
    @NotBlank(message = "URL is required.")
    private String url;

}
