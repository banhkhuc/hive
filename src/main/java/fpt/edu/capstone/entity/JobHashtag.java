package fpt.edu.capstone.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "job_hashtag")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "job_id")
    private long jobId;

    @Column(name = "hashtag_id")
    private long hashtagId;

    @Column(name = "hashtag_name")
    private String hashTagName;

    @Column(name = "status")
    private String status;

}
