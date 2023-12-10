package fpt.edu.capstone.dto.admin.user;

import java.time.LocalDateTime;

public interface AdminManageResponse {
    long getUserId();
    String getUserName();
    String getEmail();
    long getRoleId();
    String getRoleName();
    int getIsDeleted();
    LocalDateTime getLastLoginTime();
    String getAvatar();
    boolean getIsActive();
    long getAdminId();
    String getFullName();
    boolean getIsLocked();
}