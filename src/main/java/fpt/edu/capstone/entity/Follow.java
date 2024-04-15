package fpt.edu.capstone.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

//TODO : draft. need to edit logic
@Entity
@Table(name = "follow")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "follower_id")
    private long followerId;

    @Column(name = "followed_id")
    private long followedId;

    @Column(name = "type")
    private long type; // 1 = follow job, 2 = follow company, 3 = follow recruiter
}
