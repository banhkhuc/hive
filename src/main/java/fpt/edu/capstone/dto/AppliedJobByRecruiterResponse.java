package fpt.edu.capstone.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AppliedJobByRecruiterResponse {
    String getJobName();
    long getId();
    long getJobId();
    long getRecruiterId();
    long getCandidateId();
    boolean getIsApplied();
    String getApprovalStatus();
    boolean getIsUploadCv();
    String getCvUploadUrl();

}
