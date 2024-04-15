package fpt.edu.capstone.dto.admin.user;


import java.time.LocalDateTime;
import java.util.Date;

public interface CandidateManageResponse {
    long getUserId();
    long getCandidateId();
    String getAvatar();
    boolean getGender();
    Date getBirthDate();
    String getCountry();
    String getFullName();
    String getUserName();
    String getAddress();
    String getSocialLink();
    boolean getIsNeedJob();
    long getExperienceLevel();
    String getIntroduction();
    String getEmail();
    long getRoleId();
    String getRoleName();
    int getIsDeleted();
    LocalDateTime getLastLoginTime();
    boolean getIsVerifiedEmail();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    boolean getIsLocked();
    boolean getIsActive();
}

