package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "banner_active")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "is_deleted = false")
public class BannerActive extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "payment_id")
    private long paymentId;

    @Column(name = "display_position")
    private String displayPosition;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
}
