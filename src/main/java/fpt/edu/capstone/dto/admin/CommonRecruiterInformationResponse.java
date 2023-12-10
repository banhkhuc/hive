package fpt.edu.capstone.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonRecruiterInformationResponse {
    private String recruiterFullName;
    private String verifyStep;
    private long totalCreatedJob;
    private String candidateApplyPercentage;
    private String message;
    private long totalViewCV;
    private String uncheck;
}
