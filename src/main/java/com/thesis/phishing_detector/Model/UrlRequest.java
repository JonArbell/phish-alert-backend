package com.thesis.phishing_detector.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequest {

    @Pattern(
            regexp = "^(https?://.+)$",  // ✅ Only allow http/https
            message = "Invalid URL format. Only HTTP and HTTPS URLs are allowed."
    )
    @NotBlank(message = "URL is required.")
    private String url;

}
