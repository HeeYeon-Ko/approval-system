package com.example.approval_request_service.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Step {

    private Integer step;        // 단계 번호
    private Long approverId;     // 승인자 직원 ID
    private String status;       // pending, approved, rejected
    private Instant updatedAt;   // 승인/반려된 시간
}

