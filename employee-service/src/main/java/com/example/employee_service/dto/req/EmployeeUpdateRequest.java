package com.example.employee_service.dto.req;

public record EmployeeUpdateRequest(
        String department,
        String position
) {
}
