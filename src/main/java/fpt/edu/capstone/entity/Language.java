package fpt.edu.capstone.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "language")
@AllArgsConstructor
@NoArgsConstructor
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "language")
    private String language;

    @Column(name = "level")
    private String level;

    @Column(name = "cv_id")
    private long cvId;
}
