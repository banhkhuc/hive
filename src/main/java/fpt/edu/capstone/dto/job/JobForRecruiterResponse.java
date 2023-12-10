package fpt.edu.capstone.dto.job;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobForRecruiterResponse {
    private long jobId;
    private String jobName;
    private String companyName;
    private String workPlace;
    private long fromSalary;
    private long toSalary;
    private long viewCount;
    private long applyCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String flag;
    private long numberRecruits;
}
