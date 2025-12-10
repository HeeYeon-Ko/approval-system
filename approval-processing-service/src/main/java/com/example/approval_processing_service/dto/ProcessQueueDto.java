package com.example.approval_processing_service.dto;

public record ProcessQueueDto(
        int requestId,
        int requesterId,
        String title,
        String content
) {
}
