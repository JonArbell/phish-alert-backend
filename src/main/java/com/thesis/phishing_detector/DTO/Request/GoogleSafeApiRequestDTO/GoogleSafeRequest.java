package com.thesis.phishing_detector.DTO.Request.GoogleSafeApiRequestDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleSafeRequest {
    private Client client;
    private ThreatInfo threatInfo;
}
