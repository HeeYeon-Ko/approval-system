package com.example.employee_service.dto.res;

import java.time.LocalDateTime;

public record EmployeeDetailResponse(
        Long id,
        String name,
        String department,
        String position,
        LocalDateTime createdAt
) {
}
