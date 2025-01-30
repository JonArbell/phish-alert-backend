package com.thesis.phishing_detector.Model.GoogleSafeApiModel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleSafeRequest {
    private Client client;
    private ThreatInfo threatInfo;
}
