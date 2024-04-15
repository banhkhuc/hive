package fpt.edu.capstone.dto.recruiter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobDetailPurchasedResponse {
    private long id;
    private String companyName;
    private String jobName;
    private String workPlace;
    private String workForm;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private long fromSalary;
    private long toSalary;
    private long numberRecruits;
    private String rank;
    private String experience;
    private boolean gender;
    private String jobDescription;
    private String jobRequirement;
    private String benefit;
    private String fieldName;
    private boolean isPopularJob;
    private boolean isNewJob;
    private boolean isUrgentJob;
    private long recruiterId;
    private String weekday;
    private String country;
    private String flag;
}
