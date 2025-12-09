package com.example.employee_service.controller;

import com.example.employee_service.dto.req.EmployeeCreateRequest;
import com.example.employee_service.dto.req.EmployeeUpdateRequest;
import com.example.employee_service.dto.res.EmployeeDetailResponse;
import com.example.employee_service.dto.res.EmployeeIdResponse;
import com.example.employee_service.dto.res.EmployeeListResponse;
import com.example.employee_service.service.EmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // 직원 생성
    @PostMapping("/employees")
    public ResponseEntity<EmployeeIdResponse> create(@RequestBody EmployeeCreateRequest request) {
        EmployeeIdResponse response = employeeService.create(request);
        return ResponseEntity.ok(response);
    }

    // 직원 목록 조회
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeListResponse>> getEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position
    ) {
        List<EmployeeListResponse> response = employeeService.getEmployees(department, position);
        return ResponseEntity.ok(response);
    }

    // 직원 상세 조회
    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeDetailResponse> getEmployee(@PathVariable Long id) {
        EmployeeDetailResponse response = employeeService.getEmployee(id);
        return ResponseEntity.ok(response);
    }

    // 직원 수정
    @PutMapping("/employees/{id}")
    public ResponseEntity<Void> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeUpdateRequest request
    ) {
        employeeService.updateEmployee(id, request);
        return ResponseEntity.noContent().build();
    }

    // 직원 삭제
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
