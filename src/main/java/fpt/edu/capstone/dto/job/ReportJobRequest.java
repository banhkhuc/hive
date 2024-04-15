package fpt.edu.capstone.dto.job;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportJobRequest {
    private long jobId;
    private String reportReason;
    private String relatedLink;
    private String fullName;
    private String phone;
    private String userAddress;
    private String userEmail;
}
