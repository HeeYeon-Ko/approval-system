package com.example.approval_request_service.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateApprovalRequest(@NotNull Long requesterId,
                                    @NotBlank String title,
                                    @NotBlank String content,
                                    @NotEmpty List<StepRequest> steps) {
    public record StepRequest(
            @NotNull Integer step,
            @NotNull Long approverId
    ) {
    }
}
