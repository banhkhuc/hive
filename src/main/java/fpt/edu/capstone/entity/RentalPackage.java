package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "rental_package")
@Entity
@AllArgsConstructor
@NoArgsConstructor
//Các gói cho thuê
public class RentalPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //1

    @Column(name = "package_group") // quảng cáo tuyển dụng, top employer branding
    private String packageGroup;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private boolean status;
}
