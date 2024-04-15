package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.admin.user.ReportedUserResponse;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.job.ReportJobRequest;
import fpt.edu.capstone.dto.job.ReportedJobResponse;
import fpt.edu.capstone.entity.Job;
import fpt.edu.capstone.entity.Recruiter;
import fpt.edu.capstone.entity.Report;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.ReportedRepository;
import fpt.edu.capstone.service.JobService;
import fpt.edu.capstone.service.RecruiterService;
import fpt.edu.capstone.service.ReportedService;
import fpt.edu.capstone.service.UserService;
import fpt.edu.capstone.utils.Enums;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReportedServiceImpl implements ReportedService {

    private final ReportedRepository reportedRepository;

    private final UserService userService;

    private final JobService jobService;

    private final ModelMapper modelMapper;

    private final RecruiterService recruiterService;

    @Override
    public Page<ReportedUserResponse> searchReportedUsers(Pageable pageable, String username, String personReportName,
                                                          List<Long> userId, List<Long> personReportId) {
        return reportedRepository.searchReportedUsers(pageable, username, personReportName, userId, personReportId);
    }

    @Override
    public Page<ReportedJobResponse> searchReportedJob(Pageable pageable, LocalDateTime createdAtFrom, LocalDateTime createAtTo, LocalDateTime updatedAtFrom,
                                                       LocalDateTime updatedAtTo, String jobName) {
        return reportedRepository.searchReportedJob(pageable,createdAtFrom,createAtTo,updatedAtFrom,updatedAtTo, jobName);
    }

    @Override
    public Report reportJob(ReportJobRequest request, long userId) {
        if (userService.getUserById(userId) == null) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        if (jobService.getJobById(request.getJobId()) == null) {
            throw new HiveConnectException(ResponseMessageConstants.JOB_DOES_NOT_EXIST);
        }
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new HiveConnectException("Vui lòng điền họ tên của bạn.");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new HiveConnectException("Vui lòng điền họ tên của bạn.");
        }
        if (request.getUserAddress() == null || request.getUserAddress().trim().isEmpty()) {
            throw new HiveConnectException("Vui lòng điền địa chỉ của bạn.");
        }
        if (request.getUserEmail() == null || request.getUserEmail().trim().isEmpty()) {
            throw new HiveConnectException("Vui lòng nhập email.");
        }
        if (request.getReportReason() == null || request.getReportReason().trim().isEmpty()) {
            throw new HiveConnectException("Vui lòng nhập lí do báo cáo.");
        }
        Report report = modelMapper.map(request, Report.class);
        Job job = jobService.getJobById(request.getJobId());
        Recruiter recruiter = recruiterService.getRecruiterById(job.getRecruiterId());
        report.setPersonReportId(userId);
        report.setReportedUserId(recruiter.getUserId());
        report.create();
        report.setApprovalReportedStatus(Enums.ApprovalStatus.PENDING.getStatus());
        report.setUpdatedAt(null);
        report.setReportType("1");
        reportedRepository.save(report);
        if (!reportedRepository.findById(report.getId()).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.REPORT_JOB_FAIL);
        }
        return report;
    }
}
