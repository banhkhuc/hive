package fpt.edu.capstone.dto.job;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class CvAppliedJobResponse {
    private long jobId;
    private long candidateId;
    private String candidateName;
    private String avatar;
    private List<String> experienceDesc;
    private List<String> educations;
    private String careerGoal;
    private String address;
    private String approvalStatus;
    private String cvUrl;
    private long cvId;
}

