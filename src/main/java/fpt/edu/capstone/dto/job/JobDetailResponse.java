package fpt.edu.capstone.dto.job;

import fpt.edu.capstone.entity.Company;
import fpt.edu.capstone.entity.Fields;
import fpt.edu.capstone.entity.Job;
import fpt.edu.capstone.entity.JobHashtag;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class JobDetailResponse {
    private long jobId;
    private long recruiterId;
    private long companyId;
    private String companyName;
    private String jobName;
    private long fromSalary;
    private long toSalary;
    private long numberRecruits;
    private String workForm;
    private String rank;
    private String academicLevel;
    private Boolean gender;
    private String experience;
    private String workPlace;
    private long countryId;
    private String jobDescription;
    private String jobRequirement;
    private String benefit;
    private long fieldId;
    private String fieldName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<JobHashtag> jobHashtagList;
    private String weekday;
    private int isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //company information
    Company company;
    //candidate information
    private long candidateId;
    private boolean isApplied;
    private String approvalStatus;
    private boolean isFollowing;
}
