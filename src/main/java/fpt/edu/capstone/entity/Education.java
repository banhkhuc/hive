package fpt.edu.capstone.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "education")
@AllArgsConstructor
@NoArgsConstructor
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "cv_id")
    private long cvId;

    @Column(name = "school")
    private String school;

    @Column(name = "major")
    private String major;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_studying")
    private boolean isStudying;

    @Column(name = "description")
    private String description;
}
