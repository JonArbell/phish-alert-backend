package com.thesis.phishing_detector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PhishAlertPhishingDetectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhishAlertPhishingDetectorApplication.class, args);
	}

}
