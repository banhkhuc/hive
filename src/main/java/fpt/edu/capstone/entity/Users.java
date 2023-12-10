package fpt.edu.capstone.entity;

import fpt.edu.capstone.utils.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="users")
@Where(clause = "is_deleted=0")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "role_id")
    private long roleId;

    @Column(name = "is_deleted")
    private int isDeleted = 0;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "is_verified_email")
    private boolean isVerifiedEmail;

    @Column(name = "is_verified_phone")
    private boolean isVerifiedPhone;

    @Column(name = "is_active")
    private boolean isActive = false;

    @Column(name = "is_locked")
    private boolean isLocked = false;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "is_google")
    private boolean isGoogle;

    @Column(name = "lock_reason")
    private String lockReason;
}
