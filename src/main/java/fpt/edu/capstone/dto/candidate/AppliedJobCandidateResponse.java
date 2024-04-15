package fpt.edu.capstone.dto.candidate;

import fpt.edu.capstone.entity.Job;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppliedJobCandidateResponse {
    private long appliedJobId;
    private String companyAvatar;
    private String companyName;
    private Job job;
    private String approvalStatus;
    private LocalDateTime appliedJobDate;
    private LocalDateTime approvalDate;

}
