package com.example.notification_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    @NotNull
    private Long requestId;

    @NotNull
    private Long requesterId;

    @NotNull
    private String result;

    @NotNull
    private String finalResult;

    private Long rejectedBy;
}
