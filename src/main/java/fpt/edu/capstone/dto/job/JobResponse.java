package fpt.edu.capstone.dto.job;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class JobResponse {
    private long jobId;
    private long companyId;
    private long recruiterId;
    private List<String> listHashtag;
    private String companyName;
    private String jobName;
    private String jobDescription;
    private String jobRequirement;
    private String benefit;
    private long fromSalary;
    private long toSalary;
    private long numberRecruits;
    private String rank;
    private String workForm;
    private Boolean gender;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String workPlace;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int isDeleted;
    private String companyAvatar;
    private boolean isPayment;
}
