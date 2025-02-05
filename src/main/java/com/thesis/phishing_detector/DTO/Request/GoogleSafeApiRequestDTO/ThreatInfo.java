package com.thesis.phishing_detector.DTO.Request.GoogleSafeApiRequestDTO;

import com.thesis.phishing_detector.DTO.Request.UrlRequestDTO;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ThreatInfo {

    private List<String> threatTypes;
    private List<String> platformTypes;
    private List<String> threatEntryTypes;
    private List<UrlRequestDTO> threatEntries;

}
