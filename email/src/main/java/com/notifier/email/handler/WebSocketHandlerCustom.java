package com.notifier.email.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifier.email.model.EmailNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;

@Slf4j
public class WebSocketHandlerCustom implements WebSocketHandler {


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connection established on session: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        String payload = (String) message.getPayload();
        EmailNotification email = new ObjectMapper().readValue(payload, EmailNotification.class);

        session.sendMessage(new TextMessage("test, " + session + " " + payload));

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Exception occured: {} on session: {}", exception.getMessage(), session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("Connection closed on session: {} with status: {}", session.getId(), closeStatus.getCode());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
