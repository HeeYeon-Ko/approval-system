package com.example.employee_service.dto.res;

public record EmployeeListResponse(
        Long id,
        String name,
        String department,
        String position
) {
}
