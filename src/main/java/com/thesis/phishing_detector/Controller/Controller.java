package com.thesis.phishing_detector.Controller;

import com.thesis.phishing_detector.DTO.Request.UrlRequestDTO;
import com.thesis.phishing_detector.DTO.Response.UrlResponseDTO;
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

@Slf4j
@RequiredArgsConstructor
@RestController
public class Controller {

    private final UrlProcessingService urlProcessingService;

    @PostMapping("/api/check-url")
    public ResponseEntity<UrlResponseDTO> checkUrl(@RequestBody @Valid UrlRequestDTO request,
                                                        @RequestHeader("Client-Type") String clientType){

        log.info("URL Request : {}",request.getUrl());

        log.info("Client Type : {}",clientType);

        if(!"FRONTEND".equals(clientType) && !"ARDUINO".equals(clientType))
            return new ResponseEntity<>(new UrlResponseDTO("error","Forbidden"), HttpStatus.FORBIDDEN);

        var response = urlProcessingService.processUrl(request.getUrl());

        return new ResponseEntity<>(new UrlResponseDTO("success",response), HttpStatus.OK);

    }

}
