package com.example.notification_service.ws;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Slf4j
@Component
public class NotificationWebSocketHandler implements WebSocketHandler{

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        Long id = null;

        if (query != null) {
            for (String part : query.split("&")) {
                if (part.startsWith("id=")) {
                    id = Long.parseLong(part.substring(3));
                    break;
                }
            }
        }

        if (id == null) {
            log.warn("Connection without id query. Closing.");
            try {
                session.close();
            } catch (Exception ignored) {
            }
            return;
        }

        sessions.put(id, session);
        log.info("WebSocket connected. id={}, sessionId={}", id, session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("WebSocket error: {}", exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sessions.entrySet().removeIf(e -> e.getValue().getId().equals(session.getId()));
        log.info("WebSocket closed. sessionId={}", session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
