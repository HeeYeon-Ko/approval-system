package com.example.employee_service.repository;

import com.example.employee_service.entity.Employee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByDepartmentAndPosition(String department, String position);

    List<Employee> findByDepartment(String department);

    List<Employee> findByPosition(String position);
}
