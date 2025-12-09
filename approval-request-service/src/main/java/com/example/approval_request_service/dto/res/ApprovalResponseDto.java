package com.example.approval_request_service.dto.res;

import java.time.Instant;
import java.util.List;

public record ApprovalResponseDto(Long requestId,
                                  Long requesterId,
                                  String title,
                                  String content,
                                  String finalStatus,
                                  Instant createdAt,
                                  Instant updatedAt,
                                  List<StepResponse> steps) {

    public record StepResponse(
            Integer step,
            Long approverId,
            String status,
            Instant updatedAt
    ) {
    }
}
