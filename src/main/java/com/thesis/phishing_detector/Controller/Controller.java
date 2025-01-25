//package com.thesis.phishing_detector.Controller;
//
//import com.thesis.phishing_detector.Model.UrlRequest;
//import com.thesis.phishing_detector.Services.ApiService;
//import com.thesis.phishing_detector.Services.GoogleSafeBrowsingApiService;
//import com.thesis.phishing_detector.Services.OpenAiService;
//import jakarta.validation.Valid;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//
//@CrossOrigin(origins = "https://mail.google.com")
//@RestController
//public class Controller {
//
//    private final Logger logger = LoggerFactory.getLogger(Controller.class);
//
//    private final ApiService googleSafeBrowsingApiService;
//    private final ApiService openAiService;
//
//    public Controller(GoogleSafeBrowsingApiService googleSafeBrowsingApiService, OpenAiService openAiService){
//        this.googleSafeBrowsingApiService = googleSafeBrowsingApiService;
//        this.openAiService = openAiService;
//    }
//
//    @PostMapping("/check-url")
//    public ResponseEntity<Map<String, String>> checkUrl(@Valid @RequestBody UrlRequest urlRequest,
//                                                 BindingResult bindingResult){
//
//        var response = new HashMap<String, String>();
//
//        if(bindingResult.hasErrors()){
//            bindingResult.getAllErrors().forEach(error -> response.put(error.getObjectName(),error.getDefaultMessage()));
//            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//        }
//
//        try{
//
//            var url1 = "https://plus773.com/?id=103523999";
//            var url2 = "https://7game0.com/?id=584055205";
//            var url3 = "https://taya993.com?ch=14065";
//            var url4 = "https://bit.ly/498EvoM";
//            var url5 = "https://www.bat3721.online/?mt=yb02";
//            var url6 = "https://www.tab3721.online/?mt=yb11";
//            var url7 = "https://dxd.cx/64";
//            var url8 = "https://bit.ly/40yImYH";
//            var url9 = "https://www.tokopedia.com/";
//            var fb = "https://web.facebook.com/?_rdc=1&_rdr#";
//
//            var url = new URL(urlRequest.getUrl());
//
//            logger.info("Host : {}", url);
//
//            var googleResponse = googleSafeBrowsingApiService.analyzeUrl(urlRequest.getUrl());
//
//            logger.info("Check if equal : {}",googleResponse.equalsIgnoreCase("{}\n"));
//
//            if(googleResponse.equals("{}\n")){
//                var openAiResponse = openAiService.analyzeUrl(url.getHost());
//
//                logger.info("Controller Ai Response : {}",openAiResponse);
//
//                response.put("response",openAiResponse);
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            }
//
//            logger.info("Controller Google Response : {}",googleResponse);
//
//            response.put("response",googleResponse);
//
//            return new ResponseEntity<>(response, HttpStatus.OK);
//
//        }catch (Exception e){
//            response.put(e.getClass().getSimpleName(), e.getMessage());
//            logger.info("Exception response: {}", e.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//
//    }
//
//
//}
//
