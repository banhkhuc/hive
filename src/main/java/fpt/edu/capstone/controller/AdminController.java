package fpt.edu.capstone.controller;

import fpt.edu.capstone.dto.admin.LicenseApprovalResponse;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.recruiter.ApprovalLicenseRequest;
import fpt.edu.capstone.dto.recruiter.TotalRecruitmentStatistic;
import fpt.edu.capstone.dto.register.CountRegisterUserResponse;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.LogUtils;
import fpt.edu.capstone.utils.ResponseData;
import fpt.edu.capstone.utils.ResponseDataPagination;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    private final RecruiterService recruiterService;

    private final UserService userService;

    private final AdminManageService adminManageService;

    @GetMapping("/list-admin")
    @Operation(summary = "Get list admin")
    public ResponseData getListAdmin(@RequestParam(defaultValue = "0") Integer pageNo,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        //search by name, email, ...
        try {
            ResponseDataPagination pagination = adminService.getListAdmin(pageNo, pageSize);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/search-users")
    @Operation(summary = "search/get list users account (Recruiter, Candidate, Admin) for Admin App")
    public ResponseData searchUsersForAdmin(@RequestParam(defaultValue = "0") Integer pageNo,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @RequestParam(required = false) String username,
                                            @RequestParam(required = false) String email,
                                            @RequestParam(required = false) String fullName,
                                            @RequestParam(required = false, defaultValue = "0") long userId,
                                            @RequestParam(required = false) boolean isLocked,
                                            @RequestParam String tab) {
        try {
            ResponseDataPagination pagination = adminManageService.searchUsersForAdmin(tab, pageNo, pageSize, username, email, fullName, userId, isLocked);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/search-reported-users")
    @Operation(summary = "search/get list reported users for Admin")
    public ResponseData searchReportedUsers(@RequestParam(defaultValue = "0") Integer pageNo,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @RequestParam(required = false) String username,
                                            @RequestParam(required = false) String personReportName,
                                            @RequestParam(required = false) @Size(max = 1) List<Long> userId,
                                            @RequestParam(required = false) @Size(max = 1) List<Long> personReportId) {
        try {
            ResponseDataPagination pagination = adminManageService.searchReportedUsers(pageNo, pageSize, username,
                    personReportName, userId, personReportId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/count-users")
    public ResponseData countUsers() {
        try {
            HashMap<String, List<CountRegisterUserResponse>> responseHashMap = userService.countUser();
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, responseHashMap);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/count-recruitment-statistic")
    public ResponseData countTotalRecruitmentStatistic() {
        try {
            HashMap<String, Integer> responseHashMap = userService
                    .countTotalRecruitmentStatistic();
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, responseHashMap);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @PutMapping("/approve-reported")
    public ResponseData updateReportedStatus(@RequestParam String approvalStatus,
                                             @RequestParam long reportId) {
        try {
            String msg = adminManageService.approveReportedJob(approvalStatus, reportId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), msg);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/get-reported-job")
    public ResponseData getReportedJob(@RequestParam(defaultValue = "0") Integer pageNo,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(value = "createdAtFrom", required = false) String createdAtFrom,
                                       @RequestParam(value = "createdAtTo", required = false) String createdAtTo,
                                       @RequestParam(value = "updatedAtFrom", required = false) String updatedAtFrom,
                                       @RequestParam(value = "updatedAtTo", required = false) String updatedAtTo,
                                       @RequestParam(value = "jobName", defaultValue = StringUtils.EMPTY) String jobName) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime createdFrom = LocalDate.parse(createdAtFrom, formatter).atStartOfDay();
            LocalDateTime createdTo = LocalDate.parse(createdAtTo, formatter).atStartOfDay();

            LocalDateTime updatedFrom = LocalDate.parse(updatedAtFrom, formatter).atStartOfDay();
            LocalDateTime updatedTo = LocalDate.parse(updatedAtTo, formatter).atStartOfDay();

            ResponseDataPagination reports = adminManageService.
                    searchReportedJob(pageNo, pageSize, createdFrom, createdTo, updatedFrom, updatedTo, jobName);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, reports);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @PutMapping("/lock-unlock-user")
    public ResponseData lockUnlockUser(@RequestParam("userId") long userId,
                                       @RequestParam(value = "reason", defaultValue = StringUtils.EMPTY) String reason) {
        try {
            Users updateUser = userService.lockUnlockUser(userId, reason);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, updateUser);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

//    @PutMapping("/active-deactive-user")
//    public ResponseData activeDeactiveUser(@RequestParam long userId) {
//        try {
//            Users updateUser = userService.activeDeactiveUser(userId);
//            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, updateUser);
//        } catch (Exception e) {
//            String msg = LogUtils.printLogStackTrace(e);
//            logger.error(msg);
//            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//        }
//    }

    @GetMapping("/get-licenses-approval")
    @Operation(summary = "Get list license for Admin approval")
    public ResponseData searchLicensesApproval(@RequestParam(required = false) String businessApprovalStatus,
                                               @RequestParam(required = false) String additionalApprovalStatus) {
        //một record sẽ có hai dòng business license và addition license: tất cả trạng thái
        try {
            List<LicenseApprovalResponse> responseList = adminManageService.
                    searchLicenseApprovalForAdmin(businessApprovalStatus, additionalApprovalStatus);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, responseList);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PutMapping("approve-license")
    @Operation(summary = "Admin approve license")
    public ResponseData approveLicense(@RequestBody ApprovalLicenseRequest request) {
        try {
            Recruiter recruiter = recruiterService.approveLicense(request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, recruiter);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }
}
