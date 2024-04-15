package fpt.edu.capstone.dto.job;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApprovalJobRequest {
    private long jobId;
    private long candidateId;
    private String approvalStatus;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
