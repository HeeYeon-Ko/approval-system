package com.example.approval_request_service.client;

import com.example.approval_request_service.dto.req.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${notification-service.base-url}")
    private String notificationServiceBaseUrl;

    public void sendNotification(NotificationRequest request) {
        String url = notificationServiceBaseUrl + "/notifications";
        HttpEntity<NotificationRequest> entity = new HttpEntity<>(request);
        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }
}
