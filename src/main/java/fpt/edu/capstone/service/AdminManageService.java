package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.admin.LicenseApprovalResponse;
import fpt.edu.capstone.dto.banner.ApproveBannerRequest;
import fpt.edu.capstone.utils.ResponseDataPagination;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminManageService {
    List<LicenseApprovalResponse> searchLicenseApprovalForAdmin(String businessApprovalStatus,
                                                                String additionalApprovalStatus);

    ResponseDataPagination searchReportedJob(Integer pageNo, Integer pageSize, LocalDateTime createdAtFrom,
                                             LocalDateTime createAtTo, LocalDateTime updatedAtFrom,
                                             LocalDateTime updatedAtTo, String jobName);

    void approveBanner(ApproveBannerRequest request);

    ResponseDataPagination getBannerOfRecruiterForAdmin(Integer pageNo, Integer pageSize, String screenName, LocalDateTime from, LocalDateTime to);

    ResponseDataPagination searchUsersForAdmin(String selectTab, Integer pageNo, Integer pageSize, String username, String email, String fullName, long userId, boolean isLocked);

    ResponseDataPagination searchReportedUsers(Integer pageNo, Integer pageSize, String username, String personReportName,
                                               List<Long> userId, List<Long> personReportId);

    String approveReportedJob(String approvalStatus, long reportId);
}
