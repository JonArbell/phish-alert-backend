package com.thesis.phishing_detector.Bean;

import com.thesis.phishing_detector.Services.UrlProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketHandlerImpl extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(WebSocketHandlerImpl.class);

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    private UrlProcessingService urlProcessingService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        var query = session.getUri().getQuery(); // Extract query string

        String clientType = null;

        if (query != null && query.contains("clientType="))
            clientType = query.split("clientType=")[1];

        if("FRONTEND".equals(clientType) || "ARDUINO".equals(clientType)){
            sessions.add(session);
            logger.error("WebSocket connection success. Total active sessions : {}", sessions.size());
        }

        logger.error("WebSocket connection failed. Total active sessions : {}", sessions.size());

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("WebSocket connection closed. Total active sessions: {}", sessions.size());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        var urlRequest = message.getPayload();

        logger.info("Url Request : {}",urlRequest);

        try {

            var response = urlProcessingService.processUrl(urlRequest);

            for(var client  : sessions){
                if(client.isOpen())
                    client.sendMessage(new TextMessage(response));
            }

        } catch (Exception e) {

            logger.error("Error occurred while processing the URL : {} {}", urlRequest, e.getMessage());

            // Send error message to the client
            try {
                session.sendMessage(new TextMessage("An error occurred while processing the URL. Please try again later."));
            } catch (IOException ioException) {
                logger.error("Error sending error message back to client: ", ioException);
            }
        }
    }

}
