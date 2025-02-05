package com.thesis.phishing_detector.DTO.Request.OpenAiRequestDTO;

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
