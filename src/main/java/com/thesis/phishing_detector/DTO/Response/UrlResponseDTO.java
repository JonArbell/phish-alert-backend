package com.thesis.phishing_detector.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UrlResponseDTO {

    private String response;
    private String message;

}
