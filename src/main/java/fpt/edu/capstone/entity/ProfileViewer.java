package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "profile_viewer")
@Getter
@Setter
public class ProfileViewer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "cv_id")
    private long cvId;

    @Column(name = "viewer_id")
    private long viewerId;

    @Column(name = "candidate_id")
    private long candidateId;

    @Column(name = "is_save")
    private boolean isSave;
}
