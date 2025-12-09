package com.example.employee_service.dto.req;

public record EmployeeCreateRequest(
        String name,
        String department,
        String position
) {
}
