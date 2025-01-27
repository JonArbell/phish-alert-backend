package com.thesis.phishing_detector.Controller;

import com.thesis.phishing_detector.Model.UrlRequest;
import com.thesis.phishing_detector.Services.UrlProcessingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Controller {

    private final Logger logger = LoggerFactory.getLogger(Controller.class);

    private final UrlProcessingService urlProcessingService;

    public Controller(UrlProcessingService urlProcessingService){
        this.urlProcessingService = urlProcessingService;
    }

    @PostMapping("/check-url")
    public ResponseEntity<Map<String, String>> checkUrl(@Valid @RequestBody UrlRequest urlRequest,
                                                        @RequestHeader("Client-Type") String clientType,
                                                        BindingResult bindingResult){

        logger.info("URL Request : {}",urlRequest);

        logger.info("Client Type : {}",clientType);

        var response = new HashMap<String, String>();

        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(error -> response.put("error",error.getDefaultMessage()));
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

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
            logger.info("Exception response: {}", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

}

