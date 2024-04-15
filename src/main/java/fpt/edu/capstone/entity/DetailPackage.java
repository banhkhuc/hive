package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "detail_package")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DetailPackage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "rental_package_id")
    private long rentalPackageId; //1

    //QUẢNG CÁO TUYỂN DỤNG
    @Column(name = "detail_name")
    private String detailName; //tên chi tiết các gói dịch vụ

    @Column(name = "price")
    private long price;

    @Column(name = "discount")
    private long discount;

    @Column(name = "time_expired")
    private String timeExpired;

    @Column(name = "description")
    private String description;

   @Column(name = "is_related_job")
    private boolean isRelatedJob;

    @Column(name = "is_suggest_job")
    private boolean isSuggestJob;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "max_cv_view")
    private int maxCvView; // được xem full thông tin tối đa bao nhiêu cv

    @Column(name = "is_popular_job")
    private boolean isPopularJob;

    @Column(name = "is_new_job")
    private boolean isNewJob;

    @Column(name = "is_urgent_job")
    private boolean isUrgentJob;
}
