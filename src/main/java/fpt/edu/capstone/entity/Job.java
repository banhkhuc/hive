package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "job")
public class Job extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "company_id")
    private long companyId;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "work_place")
    private String workPlace;

    @Column(name = "work_form")
    private String workForm;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "from_salary")
    private long fromSalary;

    @Column(name = "to_salary")
    private long toSalary;

    @Column(name = "number_recruits")
    private long numberRecruits;

    @Column(name = "rank")
    private String rank;

    @Column(name = "experience")
    private String experience;

    @Column(name = "gender")
    private Boolean gender;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "job_requirement")
    private String jobRequirement;

    @Column(name = "benefit")
    private String benefit;

    @Column(name = "field_id")
    private long fieldId;

    @Column(name = "is_deleted")
    private int isDeleted;

    @Column(name = "recruiter_id")
    private long recruiterId;

    @Column(name = "weekday")
    private String weekday;

    @Column(name = "vietnam_country_id")
    private long countryId;

    @Column(name = "flag")
    private String flag;

    @Column(name = "academic_level")
    private String academicLevel;
}
