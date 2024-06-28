package com.service.user.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;

@Slf4j
public class CustomStompSessionHandler extends StompSessionHandlerAdapter {

    private static StompSession activeSession;


    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("New session established : " + session.getSessionId());
        session.subscribe("/specific/reply", this);
        activeSession = session;
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                byte[] payload, Throwable exception) {
        log.error("Got an exception", exception);
    }

    @Override
    public void handleFrame(StompHeaders headers, @Nullable Object payload) {

        log.info("Handling frame for {}", activeSession.getSessionId());
        assert payload != null;
        String msg = String.valueOf(payload);
        log.info(msg);
        activeSession.disconnect();

    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }
}

