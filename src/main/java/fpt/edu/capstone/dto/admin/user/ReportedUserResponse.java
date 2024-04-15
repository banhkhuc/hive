package fpt.edu.capstone.dto.admin.user;

import java.time.LocalDateTime;

public interface ReportedUserResponse {
    long getReportedId();
    long getReportedUserId();
    String getUserName();
    String getReportReason();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    long getPersonReportId();
    String getPersonReportName();
    String getRelatedLink();
    String getApprovalReportedStatus();
}
