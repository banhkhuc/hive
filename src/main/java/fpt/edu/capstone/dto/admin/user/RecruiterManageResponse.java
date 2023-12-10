package fpt.edu.capstone.dto.admin.user;

import java.time.LocalDateTime;

public interface RecruiterManageResponse {
    long getUserId();
    String getUsername();
    String email();
    long getRoleId();
    String getRoleName();
    int getIsDeleted();
    LocalDateTime getLastLoginTime();
    String getAvatar();
    boolean getIsVerifiedEmail();
    boolean getIsActive();
    long getRecruiterId();
    long getCompanyId();
    String getCompanyName();
    String getFullName();
    boolean getVerifyAccount();
    boolean getVerifyPhoneNumber();
    boolean getGender();
    String getPosition();
    String getLinkedInAccount();
    String getBusinessLicense();
    String getAdditionalLicense();
    String getPhoneNumber();
    LocalDateTime getCreateAt();
    LocalDateTime getUpdateAt();
    String getCompanyAddress();
    boolean getIsLocked();
}