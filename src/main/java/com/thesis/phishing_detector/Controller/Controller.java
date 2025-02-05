package com.thesis.phishing_detector.Controller;

import com.thesis.phishing_detector.Model.UrlRequest;
import com.thesis.phishing_detector.Services.UrlProcessingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class Controller {

    private final UrlProcessingService urlProcessingService;

    @PostMapping("/api/check-url")
    public ResponseEntity<Map<String, String>> checkUrl(@RequestBody @Valid UrlRequest request,
                                                        @RequestHeader("Client-Type") String clientType){

        log.info("URL Request : {}",request.getUrl());

        log.info("Client Type : {}",clientType);

        if(!"FRONTEND".equals(clientType) && !"ARDUINO".equals(clientType))
            return new ResponseEntity<>(Map.of("error","Forbidden"), HttpStatus.FORBIDDEN);

        var response = urlProcessingService.processUrl(request.getUrl());

        return new ResponseEntity<>(Map.of("response",response), HttpStatus.OK);

    }

}
