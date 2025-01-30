package com.thesis.phishing_detector.Model.OpenAiModel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {

    private String role;
    private String content;

}
