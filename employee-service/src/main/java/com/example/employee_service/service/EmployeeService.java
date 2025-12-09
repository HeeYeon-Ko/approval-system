package com.example.employee_service.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.employee_service.dto.req.EmployeeCreateRequest;
import com.example.employee_service.dto.req.EmployeeUpdateRequest;
import com.example.employee_service.dto.res.EmployeeDetailResponse;
import com.example.employee_service.dto.res.EmployeeIdResponse;
import com.example.employee_service.dto.res.EmployeeListResponse;
import com.example.employee_service.entity.Employee;
import com.example.employee_service.repository.EmployeeRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // 직원 생성
    public EmployeeIdResponse create(EmployeeCreateRequest request) {
        Employee employee = Employee.builder()
                .name(request.name())
                .department(request.department())
                .position(request.position())
                .build();

        Employee saved = employeeRepository.save(employee);
        return new EmployeeIdResponse(saved.getId());
    }

    // 직원 목록 조회
    @Transactional(readOnly = true)
    public List<EmployeeListResponse> getEmployees(String department, String position) {
        List<Employee> employees;

        if (department != null && position != null) {
            employees = employeeRepository.findByDepartmentAndPosition(department, position);
        } else if (department != null) {
            employees = employeeRepository.findByDepartment(department);
        } else if (position != null) {
            employees = employeeRepository.findByPosition(position);
        } else {
            employees = employeeRepository.findAll();
        }

        return employees.stream()
                .map(e -> new EmployeeListResponse(
                        e.getId(),
                        e.getName(),
                        e.getDepartment(),
                        e.getPosition()
                ))
                .toList();
    }

    // 직원 상세 조회
    @Transactional(readOnly = true)
    public EmployeeDetailResponse getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));

        return new EmployeeDetailResponse(
                employee.getId(),
                employee.getName(),
                employee.getDepartment(),
                employee.getPosition(),
                employee.getCreatedAt()
        );
    }

    // 직원 수정
    public void updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));

        if (request.department() == null || request.position() == null) {
            throw new IllegalArgumentException("department and position must not be null");
        }

        employee.updateDepartmentAndPosition(
                request.department(),
                request.position()
        );
    }

    // 직원 삭제
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
