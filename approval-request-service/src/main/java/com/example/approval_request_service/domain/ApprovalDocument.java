package com.example.approval_request_service.domain;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "approvals")
public class ApprovalDocument {

    @Id
    private String id;

    private Long requestId;     // 자동 생성되는 요청 ID
    private Long requesterId;   // 요청자 직원 ID
    private String title;       // 결재 제목
    private String content;     // 결재 내용

    private List<Step> steps;   // 결재 단계 리스트

    private String finalStatus; // in_progress, approved, rejected
    private Instant createdAt;
    private Instant updatedAt;
}

