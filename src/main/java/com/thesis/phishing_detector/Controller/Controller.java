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
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class Controller {

    private final UrlProcessingService urlProcessingService;

    @PostMapping("/api/check-url")
    public ResponseEntity<Map<String, String>> checkUrl(@RequestBody @Valid UrlRequest urlRequest,
                                                        @RequestHeader("Client-Type") String clientType){

        log.info("URL Request : {}",urlRequest.getUrl());

        log.info("Client Type : {}",clientType);

        var response = new HashMap<String, String>();

        if(!"FRONTEND".equals(clientType) && !"ARDUINO".equals(clientType)){
            response.put("error","Forbidden");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        try{

            var checkUrlResponse = urlProcessingService.processUrl(urlRequest.getUrl());

            response.put("response",checkUrlResponse);

            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e){
            response.put("error", e.getMessage());
            log.info("Exception response: {}", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

}
