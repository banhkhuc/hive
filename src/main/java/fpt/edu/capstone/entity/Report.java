package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reported")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "reported_user_id")
    private long reportedUserId;

    @Column(name = "report_reason")
    private String reportReason;

    @Column(name = "person_report_id")
    private long personReportId;

    @Column(name = "related_link")
    private String relatedLink;

    @Column(name = "approval_reported_status")
    private String approvalReportedStatus;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "user_address")
    private String userAddress;

    @Column(name = "user_email")
    private String userEmail;

    //1 job, 2 user
    @Column(name = "report_type")
    private String reportType;

    @Column(name = "job_id")
    private long jobId;
}
