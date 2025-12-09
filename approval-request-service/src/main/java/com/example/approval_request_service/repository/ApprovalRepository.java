package com.example.approval_request_service.repository;

import com.example.approval_request_service.domain.ApprovalDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApprovalRepository extends MongoRepository<ApprovalDocument, String> {

    Optional<ApprovalDocument> findByRequestId(Long requestId);
    Optional<ApprovalDocument> findTopByOrderByRequestIdDesc();
}
