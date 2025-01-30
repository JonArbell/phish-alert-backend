package com.thesis.phishing_detector.Services;

import com.thesis.phishing_detector.Model.OpenAiModel.Message;
import com.thesis.phishing_detector.Model.OpenAiModel.PromptRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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
    private String url;

    public OpenAiService(WebClient.Builder webClient){
        this.webClient = webClient
                .baseUrl("https://api.openai.com")
                .build();
    }

    @Override
    public String analyzeUrl(String url) {

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

        // This prompt is come from Deepseek AI. Best prompt.
        var prompt = "Analyze this URL: " + url + " and check if it seems like a phishing attempt. Use the following criteria to evaluate the URL:\n" +
                "1. **Domain Analysis**:\n" +
                "   - Check if the domain name is misspelled or mimics a well-known website (e.g., 'paypa1.com' instead of 'paypal.com').\n" +
                "   - Verify if the domain is newly registered (phishing domains are often created recently). Use WHOIS lookup to check the registration date.\n" +
                "   - Check if the domain uses an IP address instead of a proper domain name.\n" +
                "   - Verify if the domain uses a less common top-level domain (TLD) like .xyz, .top, or .online, which are often used in phishing attempts.\n" +
                "   - **Check if the subdomain is unrelated to the purpose of the main domain**. For example, a subdomain like 'login.google.com' is related to Google's purpose, but 'randomstring.google.com' may be suspicious.\n" +
                "2. **URL Structure**:\n" +
                "   - Look for excessive subdomains or random strings in the URL path.\n" +
                "   - Check if the URL contains suspicious keywords like 'login', 'secure', 'account', 'verify', or 'password'.\n" +
                "   - Verify if the URL uses a URL shortening service (e.g., bit.ly, tinyurl.com).\n" +
                "   - Check if the URL has unusual or unexpected file extensions (e.g., .exe, .scr, .zip).\n" +
                "3. **Content Analysis**:\n" +
                "   - Check if the website asks for sensitive information (e.g., passwords, credit card numbers, social security numbers).\n" +
                "   - Verify if the website has poor design, broken images, or inconsistent branding.\n" +
                "   - Look for grammar or spelling errors on the website.\n" +
                "   - Check if the website has a valid SSL certificate (HTTPS) and if the certificate is issued by a trusted Certificate Authority (CA).\n" +
                "   - Verify if the website has a privacy policy, terms of service, and contact information.\n" +
                "4. **Behavioral Analysis**:\n" +
                "   - Check if the URL redirects to unexpected or multiple destinations.\n" +
                "   - Verify if the website generates excessive pop-ups or alerts.\n" +
                "   - Check if the website attempts to download files automatically or prompts you to install software.\n" +
                "   - Verify if the website behaves differently in different browsers or devices.\n" +
                "5. **SEO and Context**:\n" +
                "   - Check if the URL is associated with a school, organization, or legitimate business.\n" +
                "   - Verify if the website has proper SEO (e.g., meta tags, descriptions) or appears in trusted search engine results.\n" +
                "   - Check if the website has social media profiles and if they are active and legitimate.\n" +
                "6. **External Verification**:\n" +
                "   - Use tools like VirusTotal, Google Safe Browsing, or PhishTank to check if the URL is flagged as malicious.\n" +
                "   - Search for the URL or domain name online to see if it has been reported as a phishing site.\n" +
                "   - Check if the website is listed in any known phishing databases.\n" +
                "\n" +
                "Based on the above analysis, provide a verdict with a confidence level of at least 80%. Only respond with 'Safe' or 'Suspicious'. If the URL contains any of the following characteristics, classify it as 'Suspicious':\n" +
                "   - Misspelled or mimicked domain names.\n" +
                "   - Newly registered domains (less than 6 months old).\n" +
                "   - URLs with excessive subdomains or random strings.\n" +
                "   - Subdomains that are unrelated to the purpose of the main domain.\n" +
                "   - Websites asking for sensitive information without a valid reason.\n" +
                "   - Poor website design, grammar errors, or inconsistent branding.\n" +
                "   - Lack of a valid SSL certificate or HTTPS.\n" +
                "   - URLs flagged by external tools like VirusTotal or Google Safe Browsing.\n" +
                "   - Repository names containing any suspicious keywords. Examples 'viral', 'free', 'download', " +
                "'video', etc.\n" +
                "   - Unknown or newly created user profiles hosting the repository.\n" +
                "\n" +
                "If the URL does not meet any of the suspicious criteria and appears legitimate, classify it as 'Safe'.";

        logger.info("OPEN AI | URL : {}",url);

        var systemMessage = Message.builder()
                .role("system")
                .content("You are a phishing detection expert. Analyze the URL and respond only with 'Safe' or 'Suspicious'.")
                .build();

        var userMessage = Message.builder()
                .role("user")
                .content(prompt)
                .build();

        var requestBody = PromptRequest.builder()
                .model(this.model)
                .messages(List.of(systemMessage,userMessage))
                .max_tokens(this.MAX_TOKEN)
                .build();

        return webClient.post()
                .uri(this.url)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+this.apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(response -> logger.info("Ai Response : {}",response))
                .doOnError(error -> {
                    logger.error("Ai Error : {}",error.getMessage());
                    throw new RuntimeException(error.getMessage());
                })
                .map(response -> {

                    var choices = (Map<String, Object>) ((List<?>) response.get("choices")).get(0);

                    logger.info("Choices : {}",choices);

                    var message = (Map<String, String>) choices.get("message");

                    logger.info("Message : {}",message);

                    return message.get("content");
                })
                .block();

    }
}
