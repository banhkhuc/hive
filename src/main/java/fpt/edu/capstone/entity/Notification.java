package fpt.edu.capstone.entity;

import io.micrometer.core.annotation.Counted;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "receiver_id")
    private long receiverId;

    @Column(name = "type")
    private long type;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "content")
    private String content;

    @Column(name = "is_seen")
    private boolean isSeen;

    @Column(name = "is_delete")
    private boolean isDelete;

    @Column(name = "target_id")
    private long targetId;



}
