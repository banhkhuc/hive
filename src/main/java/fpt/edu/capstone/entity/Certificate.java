package fpt.edu.capstone.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "certificate")
@AllArgsConstructor
@NoArgsConstructor
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "certificate_name")
    private String certificateName;

    @Column(name = "certificateUrl")
    private String certificateUrl;

    @Column(name = "status")
    private long status;

    @Column(name = "cv_id")
    private long cvId;
}
