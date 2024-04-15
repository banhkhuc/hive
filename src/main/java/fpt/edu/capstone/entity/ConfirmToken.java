package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "confirm_token")
public class ConfirmToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private long tokenId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "expired_time")
    private LocalDateTime expiredTime;

    public ConfirmToken(long userId){
        this.userId = userId;
        confirmationToken = UUID.randomUUID().toString();
        create();
        expiredTime = getCreatedAt().plusMinutes(30); //Sau 30p token se expired
    }
}
