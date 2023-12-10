package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recruiter")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recruiter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "company_id")
    private long companyId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "verify_account")
    private boolean verifyAccount;

    @Column(name = "gender")
    private boolean gender;

    @Column(name = "position")
    private String position;

    @Column(name = "linkedin_url")
    private String linkedinAccount;

    @Column(name = "additional_license")
    private String additionalLicense;

    @Column(name = "business_license_url")
    private String businessLicenseUrl;

    @Column(name = "additional_license_url")
    private String additionalLicenseUrl;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "business_license_approval_status")
    private String businessLicenseApprovalStatus;

    @Column(name = "additional_license_approval_status")
    private String additionalLicenseApprovalStatus;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "total_cv_view")
    private Integer totalCvView;
}
