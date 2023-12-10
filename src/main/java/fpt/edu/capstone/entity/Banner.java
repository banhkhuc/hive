package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "banner")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Banner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "rental_package_id")
    private long rentalPackageId;

    @Column(name = "price")
    private long price;

    @Column(name = "discount")
    private long discount;

    @Column(name = "time_expired")
    private String timeExpired;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;
    //Vị trí các banner
    @Column(name = "spotlight")
    private boolean spotlight;

    @Column(name = "homepage_banner_a")
    private boolean homepageBannerA;

    @Column(name = "homepage_banner_b")
    private boolean homepageBannerB;

    @Column(name = "homepage_banner_c")
    private boolean homepageBannerC;

    @Column(name = "job_banner_a")
    private boolean jobBannerA;

    @Column(name = "job_banner_b")
    private boolean jobBannerB;

    @Column(name = "job_banner_c")
    private boolean jobBannerC;

    @Column(name = "is_deleted")
    private boolean isDeleted;

}
