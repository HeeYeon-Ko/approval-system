package com.example.approval_request_service.dto.req;

public record NotificationRequest(
        Long requestId,
        Long requesterId,
        String result,
        String finalResult,
        Long rejectedBy
) {
}
