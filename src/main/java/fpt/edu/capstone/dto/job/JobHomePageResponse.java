package fpt.edu.capstone.dto.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobHomePageResponse {
    private long id;
    private long companyId;
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
    private long recruiterId;
    private String weekday;
    private long countryId;
    private String flag;
    private String companyAvatar;
    private List<String> listHashtag;
    private String companyName;
}
