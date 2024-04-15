package fpt.edu.capstone.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "other_skill")
@AllArgsConstructor
@NoArgsConstructor
public class OtherSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "skill_name")
    private String skillName;

    @Column(name = "cv_id")
    private long cvId;

    @Column(name = "level")
    private String level;
}
