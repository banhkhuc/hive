package fpt.edu.capstone.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "major_level")
@AllArgsConstructor
@NoArgsConstructor
public class MajorLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "field_id")
    private long fieldId;

    @Column(name = "major_id")
    private long majorId;

    @Column(name = "cv_id")
    private long cvId;

    @Column(name = "level")
    private String level;

    @Column(name = "status")
    private boolean status;
}
