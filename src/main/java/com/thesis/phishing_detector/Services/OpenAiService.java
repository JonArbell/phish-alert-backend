package com.thesis.phishing_detector.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService implements ApiService{

    private final WebClient webClient;

    private final Logger logger = LoggerFactory.getLogger(OpenAiService.class);

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final int MAX_TOKEN = 50;

    @Value("${openai.api.url}")
    private String uri;

    public OpenAiService(WebClient.Builder webClient){
        this.webClient = webClient
                .baseUrl("https://api.openai.com")
                .build();
    }

    @Override
    public String analyzeUrl(String url) {

        try{
            //        var prompt = "\"Analyze this URL: "+url+" and check if it seems like a phishing attempt. Common phishing " +
//                "tactics include:\n" +
//                "- Fake login pages that ask for personal information.\n" +
//                "- URLs that try to impersonate well-known websites (e.g., slightly altered domain names).\n" +
//                "- Redirecting to unexpected destinations.\n" +
//                "\n" +
//                "Is this URL 'Safe' or 'Suspicious'? Used these tactics along with a confidence level 80% confident." +
//                ".\"\nPlease explain why it's safe or suspicious. 300 words only";

            // 2nd best prompt
//        var prompt = "\"Analyze this URL: "+url+" and check if it seems like a phishing attempt. Common phishing " +
//                "tactics include:\n" +
//                "- Fake login pages that ask for personal information.\n" +
//                "- Check if it has SEO.\n"+
//                "- Check if it is for school or any organization.\n"+
//                "- URLs that try to impersonate well-known websites (e.g., slightly altered domain names).\n" +
//                "- Redirecting to unexpected destinations.\n" +
//                "\n" +
//                "Is this URL 'Safe' or 'Suspicious'? Used these tactics along with a confidence level 80% " +
//                "confident" +
//                "." +
//                ".\"\nPlease do not provide any additional information, just the verdict. Safe or Suspicious only";

//            var requestBody = Map.of(
//                    "model", this.model,
//                    "messages", List.of(
//                            Map.of("role", "system", "content", "You are a phishing detection expert. Use trusted search engines to verify the domain's legitimacy and focus on evidence-based results."),
//                            Map.of("role", "user", "content", prompt)
//                    ),
//                    "max_tokens", MAX_TOKEN
//            );

            // This prompt is come from Deepseek AI. Best prompt
            var prompt = "Analyze this URL: " + url + " and check if it seems like a phishing attempt. Use the following criteria to evaluate the URL:\n" +
                    "1. **Domain Analysis**:\n" +
                    "   - Check if the domain name is misspelled or mimics a well-known website (e.g., 'paypa1.com' instead of 'paypal.com').\n" +
                    "   - Verify if the domain is newly registered (phishing domains are often created recently).\n" +
                    "   - Check if the domain uses an IP address instead of a proper domain name.\n" +
                    "2. **URL Structure**:\n" +
                    "   - Look for excessive subdomains or random strings in the URL path.\n" +
                    "   - Check if the URL contains suspicious keywords like 'login', 'secure', 'account', or 'verify'.\n" +
                    "   - Verify if the URL uses a URL shortening service (e.g., bit.ly, tinyurl.com).\n" +
                    "3. **Content Analysis**:\n" +
                    "   - Check if the website asks for sensitive information (e.g., passwords, credit card numbers).\n" +
                    "   - Verify if the website has poor design, broken images, or inconsistent branding.\n" +
                    "   - Look for grammar or spelling errors on the website.\n" +
                    "4. **Behavioral Analysis**:\n" +
                    "   - Check if the URL redirects to unexpected or multiple destinations.\n" +
                    "   - Verify if the website generates excessive pop-ups or alerts.\n" +
                    "5. **SEO and Context**:\n" +
                    "   - Check if the URL is associated with a school, organization, or legitimate business.\n" +
                    "   - Verify if the website has proper SEO (e.g., meta tags, descriptions) or appears in trusted search engine results.\n" +
                    "\n" +
                    "Based on the above analysis, provide a verdict with a confidence level of at least 80%. Only respond with 'Safe' or 'Suspicious'.";

            var host = new URL(url);

            var openAiFullUrl = host.getProtocol()+"://"+host.getHost();

            logger.info("Full url : {}",openAiFullUrl);

            logger.info("API KEY {}", this.apiKey);


            // This content is come from Deepseek AI. Best content
            var requestBody = Map.of(
                    "model", this.model,
                    "messages", List.of(
                            Map.of("role", "system", "content", "You are a phishing detection expert. Analyze the URL and respond only with 'Safe' or 'Suspicious'."),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "max_tokens", MAX_TOKEN
            );

            return webClient.post()
                    .uri(this.uri)
                    .header(HttpHeaders.AUTHORIZATION,"Bearer "+this.apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .doOnNext(response -> logger.info("Ai Response : {}",response))
                    .doOnError(error -> logger.error("Ai Error : {}",error.getMessage()))
                    .map(response -> {

                        var choices = (Map<String, Object>) ((List<?>) response.get("choices")).get(0);

                        logger.info("Choices : {}",choices);

                        var message = (Map<String, String>) choices.get("message");

                        logger.info("Message : {}",message);

                        return message.get("content");
                    })
                    .block();
        }catch (Exception e){
            return e.getMessage();
        }

    }
}
