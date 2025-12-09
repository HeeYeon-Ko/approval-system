package com.example.notification_service.controller;

import com.example.notification_service.dto.NotificationRequest;
import com.example.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/notifications")
    public ResponseEntity<Void> notify(@RequestBody @Valid NotificationRequest request) {
        notificationService.sendNotification(request);
        return ResponseEntity.ok().build();
    }
}
