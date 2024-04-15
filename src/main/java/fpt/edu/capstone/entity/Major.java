package fpt.edu.capstone.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "major")
@AllArgsConstructor
@NoArgsConstructor
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "field_id")
    private long fieldId;

    @Column(name = "major_name")
    private String majorName;

    @Column(name = "status")
    private String status;
}
