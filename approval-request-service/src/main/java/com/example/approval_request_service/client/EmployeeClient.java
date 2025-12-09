package com.example.approval_request_service.client;

import com.example.approval_request_service.dto.res.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EmployeeClient {

    private final RestTemplate restTemplate;
    @Value("${employee-service.base-url}")
    private String employeeServiceBaseUrl;

    // requesterId/approverId 존재 여부 검증
    public void validateEmployeeExists(Long employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("EmployeeId must not be null");
        }

        String url = employeeServiceBaseUrl + "/employees/{id}";

        try {
            restTemplate.getForEntity(url, EmployeeResponse.class, employeeId);
            // 200이면 그냥 통과
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // 직원이 없는 경우 → 우리가 정의한 예외로 변환
                throw new IllegalArgumentException("Employee not found: " + employeeId);
            }
            // 그 외 4xx/5xx
            throw e;
        }
    }
}
