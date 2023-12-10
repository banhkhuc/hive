package fpt.edu.capstone.dto.job;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;
@Getter
@Setter
public class DetailJobResponse {
    private long id;
    private long companyId;
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
    private Boolean gender;
    private String jobDescription;
    private String jobRequirement;
    private String benefit;
    private long fieldId;
    private String fieldName;
    private long recruiterId;
    private String recruiterName;
    private String weekday;
    private long countryId;
    private String countryName;
    private String avatar;
}
