package com.thesis.phishing_detector.Model.OpenAiModel;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PromptRequest {

    private String model;
    private List<Message> messages;
    private int max_tokens;

}
