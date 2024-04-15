package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
@Table(name = "companies")
@Setter
@Getter
public class Company extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    //mandatory
    @Column(name = "field_work")
    private String fieldWork;
    //mandatory
    @Column(name = "name")
    private String name;
    //mandatory
    @Column(name = "email")
    private String email;
    //mandatory
    @Column(name = "phone")
    private String phone;

    @Column(name = "description")
    private String description;

    @Column(name = "website")
    private String website;
    //mandatory
    @Column(name = "number_employees")
    private String numberEmployees; //số lượng nhân viên đang có trong công ty
    //mandatory
    @Column(name = "address")
    private String address;
    //mandatory
    @Column(name = "taxcode")
    @Length(min = 10, max = 13)
    private String taxCode;

    @Column(name = "is_deleted")
    private int isDeleted = 0;

    @Column(name = "map_url")
    private String mapUrl;

    @Column(name = "creator_id") //Id của thằng recruiter tạo công ty
    private long creatorId;

    @Column(name = "is_locked")
    private boolean isLocked;
}
