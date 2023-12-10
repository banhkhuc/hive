package fpt.edu.capstone.dto.recruiter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalLicenseRequest {
    long userId;
    String type;
    String approvalStatus;
}
