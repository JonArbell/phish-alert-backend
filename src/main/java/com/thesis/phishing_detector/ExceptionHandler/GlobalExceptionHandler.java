package com.thesis.phishing_detector.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            log.info("Method Argument Not Valid Exception : {} - {}",error.getObjectName(),error.getDefaultMessage());
            errors.put("error", error.getDefaultMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Map<String, String>> handleWebClientRequestException(WebClientRequestException ex) {
        log.info("Web Client Request Exception : {}",ex.getMessage());
        return new ResponseEntity<>(Map.of("error","Request failed due to an error while sending the request. Please check the request details and try again."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, String>> handleWebClientResponseException(WebClientResponseException ex) {
        log.info("Web Client Response Exception : {}",ex.getMessage());
        return new ResponseEntity<>(Map.of("error","Received an invalid response from the API. Please try again later."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Map<String, String>> handleTimeoutException(TimeoutException ex) {
        log.info("Web Client Timeout Exception : {}",ex.getMessage());
        return new ResponseEntity<>(Map.of("error","Request timed out. The server took too long to respond. Please try again later."), HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(DecodingException.class)
    public ResponseEntity<Map<String, String>> handleDecodingException(DecodingException ex) {
        log.info("Web Client Decoding Exception : {}",ex.getMessage());
        return new ResponseEntity<>(Map.of("error","Error decoding the response. The server returned an unexpected format. Please try again later."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        log.info("Exception Error : {}",ex.getMessage());
        return new ResponseEntity<>(Map.of("error","An unexpected error occurred. Please try again later."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
