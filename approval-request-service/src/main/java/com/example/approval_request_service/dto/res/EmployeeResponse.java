package com.example.approval_request_service.dto.res;

public record EmployeeResponse(
        Long id,
        String name,
        String department,
        String position
) {
}
