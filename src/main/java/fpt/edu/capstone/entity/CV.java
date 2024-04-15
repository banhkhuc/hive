package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cv")
@AllArgsConstructor
@NoArgsConstructor
public class CV extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "candidate_id")
    private long candidateId;

    @Column(name = "is_deleted")
    private long isDeleted;

    @Column(name = "summary")
    private String summary;

    @Column(name = "total_experience_year")
    private String totalExperienceYear;
}
