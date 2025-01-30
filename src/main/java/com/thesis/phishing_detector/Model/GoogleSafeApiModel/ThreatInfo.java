package com.thesis.phishing_detector.Model.GoogleSafeApiModel;

import com.thesis.phishing_detector.Model.UrlRequest;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ThreatInfo {

    private List<String> threatTypes;
    private List<String> platformTypes;
    private List<String> threatEntryTypes;
    private List<UrlRequest> threatEntries;

}
