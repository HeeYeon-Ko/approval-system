package com.example.notification_service.service;

import com.example.notification_service.dto.NotificationRequest;
import com.example.notification_service.ws.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationWebSocketHandler handler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendNotification(NotificationRequest req) {
        WebSocketSession session = handler.getSessions().get(req.getRequesterId());
        if (session == null || !session.isOpen()) {
            log.warn("No active session for requesterId={}", req.getRequesterId());
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(req);
            session.sendMessage(new TextMessage(json));
            log.info("Sent notification to {}: {}", req.getRequesterId(), json);
        } catch (Exception e) {
            log.error("Failed to send notification", e);
        }
    }
}
