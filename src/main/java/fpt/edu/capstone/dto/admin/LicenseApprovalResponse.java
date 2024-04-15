package fpt.edu.capstone.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class LicenseApprovalResponse {
    private long requestUserId;
    private String requestUserName;
    private String businessLicenseUrl;
    private String businessLicenseApprovalStatus;
    private String additionalLicenseUrl;
    private String additionalLicenseApprovalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
