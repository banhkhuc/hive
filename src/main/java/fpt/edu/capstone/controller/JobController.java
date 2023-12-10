package fpt.edu.capstone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.capstone.dto.admin.CommonRecruiterInformationResponse;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.job.*;
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
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/job")
@AllArgsConstructor
public class JobController {
    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    private final JobService jobService;

    private final CandidateJobService candidateJobService;

    private final RecruiterManageService recruiterManageService;

    private final CandidateManageService candidateManageService;

    private final NotificationService notificationService;

    private final FollowService followService;

    private final CandidateService candidateService;

    private final RecruiterService recruiterService;

    private final ReportedService reportedService;

    @PostMapping("/create-job")
    public ResponseData createJob(@RequestBody @Valid CreateJobRequest request) {
        try {
            if (request == null) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.DATA_INVALID);
            }
            CommonRecruiterInformationResponse commonInfor = recruiterManageService.getCommonInforOfRecruiter(request.getRecruiterId());
            int totalStep = 4;
            if(!commonInfor.getVerifyStep().equals(totalStep + " / " + totalStep)) {
                throw new HiveConnectException(commonInfor.getMessage());
            }
            jobService.createJob(request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.CREATE_JOB_SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    //TODO: GET ALL DATA HOMEPAGE
    @GetMapping("/all-job-data-homepage")
    public ResponseData getAllDataHomepage() {
        try {
            HomePageData data = jobService.getDataHomePage();
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), data);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/find-job")
    public ResponseData searchListJobFilter(@RequestParam(defaultValue = "0") Integer pageNo,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @RequestParam(defaultValue = "0", value = "fieldId", required = false) long fieldId,
                                            @RequestParam(defaultValue = "0", value = "countryId", required = false) long countryId,
                                            @RequestParam(defaultValue = StringUtils.EMPTY, value = "jobName", required = false) String jobName,
                                            @RequestParam(defaultValue = StringUtils.EMPTY, value = "workForm", required = false) String workForm,
                                            @RequestParam(defaultValue = StringUtils.EMPTY, value = "workPlace", required = false) String workPlace,
                                            @RequestParam(defaultValue = "0", required = true) long fromSalary,
                                            @RequestParam(defaultValue = "0", required = true) long toSalary) {

        //jobName meaning common search : jobName, rank, companyName, workPlace, workForm
        try {
            ResponseDataPagination pagination = jobService.searchListJobFilter(pageNo, pageSize, fieldId, countryId, jobName, workForm, workPlace, fromSalary, toSalary);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/list-job-by-recruiter")
    @Operation(summary = "list job theo recruiterId, để hiển thị trên màn job của ứng viên")
    public ResponseData recruiterBuyPackage(@RequestParam(value = "recruiterId") long recruiterId) {
        try {
            List<Job> jobs = jobService.getJobByRecruiterId(recruiterId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.SUCCESS, jobs);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/job-detail/{id}")
    public ResponseData getJobDetail(@PathVariable("id") long jobId, @RequestParam("candidateId") long candidateId) {
        try {
            JobDetailResponse jobDetailResponse = candidateJobService.getJobDetail(jobId, candidateId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), jobDetailResponse);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.NOTFOUND.getStatus(), e.getMessage());
        }
    }

    @PutMapping("update-job")
    public ResponseData updateJob(@RequestBody UpdateJobRequest request) {
        try {
            jobService.updateJob(request);
            //Add notification
            Optional<List<Follow>> followsOP = followService.getAllFollowerOfAJob(request.getJobId());
            if (followsOP.isPresent()) { //Neu co nguoi theo doi moi add notification
                List<Follow> follows = followsOP.get();
                String content = "Công việc" + request.getJobName() + "đã có sự thay đổi, Ấn để xem";
                for (Follow f : follows) {
                    Optional<Candidate> c = candidateService.findById(f.getFollowerId());
                    Notification notification = new Notification(0, c.get().getUserId(), 6, LocalDateTime.now(), content, false, false, f.getFollowerId());
                    notificationService.insertNotification(notification);
                }
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.UPDATE_JOB_SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @DeleteMapping("/delete-job")
    public ResponseData deleteJob(@RequestParam(value = "id") long jobId) {
        try {
            jobService.deleteJob(jobId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/apply-job")
    public ResponseData applyJob(@RequestBody AppliedJobRequest request) {
        try {
            candidateManageService.appliedJob(request);
            //Add Notification
            Job j = jobService.getJobById(request.getJobId());
            Recruiter r = recruiterService.getRecruiterById(j.getRecruiterId());
            Notification notification = new Notification(0, r.getUserId(), 1, LocalDateTime.now(), j.getJobName() + " " + "vừa nhận được lượt ứng tuyển mới", false, false, j.getId());
            notificationService.insertNotification(notification);

            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS,
                    new ObjectMapper().writeValueAsString(request));
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    //Recruiter: Get list CV applied a job
    @GetMapping("/list-cv-applied-a-job/{jobId}")
    public ResponseData getListCandidateAppliedJob(@RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @PathVariable long jobId) {
        try {
            ResponseDataPagination pagination = recruiterManageService.getCvListAppliedJob(pageNo, pageSize, jobId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                    ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/popular-job")
    public ResponseData getPopularJob(@RequestParam(defaultValue = "0") Integer pageNo,
                                      @RequestParam(defaultValue = "4") Integer pageSize) {
        // get list lấy ra công việc phỏ biến
        try {
            ResponseDataPagination pagination = candidateJobService.getPopularJob(pageNo, pageSize);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/new-job")
    public ResponseData getListNewJob(@RequestParam(defaultValue = "0") Integer pageNo,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            ResponseDataPagination pagination = candidateJobService.getNewestJob(pageNo, pageSize);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/urgent-job")
    public ResponseData getListUrgentJob(@RequestParam(defaultValue = "0") Integer pageNo,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            ResponseDataPagination pagination = candidateJobService.getUrgentJob(pageNo, pageSize);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/suggest-job/{id}")
    public ResponseData getListSuggestJobByCV(@PathVariable("id") long candidateId) {
        // get list lấy ra công việc gợi ý theo cv của candidate
        //dựa theo major-level của candidate
        try {
            List<JobResponse> listSuggestJob = jobService.getListSuggestJobByCv(candidateId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, listSuggestJob);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/remote-job")
    public ResponseData getListRemoteJob(@RequestParam(defaultValue = "0") Integer pageNo,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            ResponseDataPagination pagination = candidateJobService.getListJobByWorkForm(pageNo, pageSize, "REMOTE");
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/fulltime-job")
    public ResponseData getListFulltimeJob(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            ResponseDataPagination pagination = candidateJobService.getListJobByWorkForm(pageNo, pageSize, "FULLTIME");
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/parttime-job")
    public ResponseData getListParttimeJob(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            ResponseDataPagination pagination = candidateJobService.getListJobByWorkForm(pageNo, pageSize, "PARTTIME");
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/job-by-field")
    public ResponseData getJobByCareer(@RequestParam(defaultValue = "0") Integer pageNo,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(value = "id") long fieldId) {
        try {
            ResponseDataPagination pagination = jobService.getJobByFieldId(pageNo, pageSize, fieldId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @PutMapping("/approve-job")
    public ResponseData approveJob(@RequestBody ApprovalJobRequest approvalJobRequest) {
        try {
            candidateJobService.approveJob(approvalJobRequest);

            //Add Notification
            String appr = approvalJobRequest.getApprovalStatus().equals("Approved") ? " chấp thuận" : " từ chối";
            Job j = jobService.getJobById(approvalJobRequest.getJobId());
            String content = "Đơn ứng tuyển của bạn vào " + j.getJobName() + " vừa được" + appr +"\n " + approvalJobRequest.getMessage();
            Candidate c = candidateService.getCandidateById(approvalJobRequest.getCandidateId());
            Notification notification = new Notification(0, c.getUserId(), 2, LocalDateTime.now(), content, false, false, j.getId());
            notificationService.insertNotification(notification);

            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/get-jobs-of-recruiter")
    public ResponseData getJobListOfRecruiter(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam("recruiterId") long recruiterId,
                                              @RequestParam(defaultValue = StringUtils.EMPTY, required = false) String jobName) {
        try {
            ResponseDataPagination pagination = recruiterManageService.getJobOfRecruiter(pageNo, pageSize, recruiterId, jobName);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/get-jobs-by-company")
    public ResponseData getJobByCompany(@RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam("companyId") long companyId) {
        try {
            ResponseDataPagination pagination = candidateManageService.getJobsOfCompany(pageNo, pageSize, companyId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/same-jobs-of-other-companies")
    public ResponseData getSameJobsOfOtherCompanies(@RequestParam long detailJobId) {
        try {
            List<JobResponse> responseList = jobService.getSameJobsOtherCompanies(detailJobId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, responseList);
        } catch (HiveConnectException e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @PostMapping("/report-job/{userId}")
    public ResponseData reportJob(@PathVariable("userId") long userId,
                                  @RequestBody ReportJobRequest request) {
        try {
            Report report = reportedService.reportJob(request, userId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, report);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PutMapping("/draft-job/{jobId}") // Gỡ bài ???
    public ResponseData draftJob(@PathVariable("jobId") long jobId){
        try {
            jobService.draftJob(jobId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.HIDE_JOB_SUCCESS);
        } catch (Exception e){
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }
}
