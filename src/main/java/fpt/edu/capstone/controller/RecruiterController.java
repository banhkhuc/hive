package fpt.edu.capstone.controller;

import fpt.edu.capstone.dto.AppliedJobByRecruiterResponse;
import fpt.edu.capstone.dto.admin.CommonRecruiterInformationResponse;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.recruiter.ReceiveRequestJoinCompanyResponse;
import fpt.edu.capstone.dto.recruiter.RecruiterProfileResponse;
import fpt.edu.capstone.dto.recruiter.RecruiterUpdateProfileRequest;
import fpt.edu.capstone.dto.recruiter.SentRequestJoinCompanyResponse;
import fpt.edu.capstone.dto.recruiter.UploadBusinessLicenseRequest;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/recruiter")
@AllArgsConstructor
public class RecruiterController {

    private static final Logger logger = LoggerFactory.getLogger(RecruiterController.class);

    private final RecruiterService recruiterService;

    private final RecruiterManageService recruiterManageService;

    private final RequestJoinCompanyService requestJoinCompanyService;

    private final CompanyService companyService;

    private final NotificationService notificationService;

    private final ProfileViewerService profileViewerService;

    private final AppliedJobService appliedJobService;

    @GetMapping("/recruiter-profile/{userId}")
    public ResponseData getRecruiterProfile(@PathVariable("userId") long userId) {
        try {
            RecruiterProfileResponse recruiterProfile = recruiterManageService.getRecruiterProfile(userId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), recruiterProfile);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/insert-recruiter")
    public ResponseData insertRecruiter(@RequestBody long userId) {
        try {
            Optional<Recruiter> recruiter = recruiterService.findRecruiterByUserId(userId);
            if (recruiter.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "That user id is existed", userId);
            }
            Recruiter recruiter1 = recruiterService.insertRecruiter(userId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Insert recruiter successful", recruiter1);
        } catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @PutMapping("/update-recruiter-profile/{recruiterId}")
    public ResponseData updateProfile(@PathVariable("recruiterId") long recruiterId,
                                      @RequestBody RecruiterUpdateProfileRequest request) {
        try {
            RecruiterProfileResponse profileResponse = recruiterManageService.
                    updateRecruiterInformation(recruiterId, request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.UPDATE_SUCCESSFULLY, profileResponse);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("get-list-applied-job")
    public ResponseData getListAppliedJob(long recruiterId) {
        try {
            List<AppliedJobByRecruiterResponse> appliedJobByRecruiterResponses = recruiterService.getListAppliedByForRecruiter(recruiterId);
            System.out.println(appliedJobByRecruiterResponses);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "asd", appliedJobByRecruiterResponses);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    //create request
    @PostMapping("/send-request-join-company") //không cần gửi approval id
    public ResponseData sendRequestJoinCompany(@RequestBody RequestJoinCompany requestJoinCompany) {
        try {
            Optional<RequestJoinCompany> op = requestJoinCompanyService.getSentRequest(requestJoinCompany.getSenderId());
            if(op.isPresent()) {
                requestJoinCompanyService.deleteOldSentRequest(op.get().getId());
            }
            requestJoinCompanyService.createRequest(requestJoinCompany);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Send request successful", requestJoinCompany);

        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }



    //fetch request by sender_id
    @GetMapping("/get-sent-request")
    @Operation(summary = "recruiter gửi request xem là nó đã gửi request chưa")
    public ResponseData getSentRequest(@RequestParam long senderId) {
        try {
            Optional<RequestJoinCompany> requestJoinCompanyOp = requestJoinCompanyService.getSentRequest(senderId);
            if (requestJoinCompanyOp.isPresent()) {
                Optional<Company> companyOptinal = companyService.findById(requestJoinCompanyOp.get().getCompanyId());
                if(!companyOptinal.isPresent()) {
                    return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.COMPANY_DOES_NOT_EXIST);
                }
                SentRequestJoinCompanyResponse sentRequestJoinCompanyResponse = new SentRequestJoinCompanyResponse();
                sentRequestJoinCompanyResponse.setId(requestJoinCompanyOp.get().getId());
                sentRequestJoinCompanyResponse.setApproverId(requestJoinCompanyOp.get().getApproverId());
                sentRequestJoinCompanyResponse.setStatus(requestJoinCompanyOp.get().getStatus());
                sentRequestJoinCompanyResponse.setCompany(companyOptinal.get());

                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, sentRequestJoinCompanyResponse);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.NO_REQUEST_JOIN_COMPANY_SENT);
        } catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }


    @PutMapping("/approve-join-company-request")
    @Operation(summary = "recruiter thực hiện approve/deny request company")
    public ResponseData getReceiveRequest(@RequestBody RequestJoinCompany newRequestJoinCompany) {
        try {

            //Add notification

            String appr = newRequestJoinCompany.getStatus().toLowerCase().equals("deny") ? " đã bị từ chối" : " đã được chấp thuận";
            Optional<Company> company = companyService.findById(newRequestJoinCompany.getCompanyId());
            String content = "Yêu cầu vào công ty " + company.get().getName() + appr;
            Recruiter r = recruiterService.getRecruiterById(newRequestJoinCompany.getSenderId());
            Notification notification = new Notification(0, r.getUserId(), 3, LocalDateTime.now(), content, false, false, company.get().getId());
            notificationService.insertNotification(notification);


            requestJoinCompanyService.approveRequest(newRequestJoinCompany.getStatus(), newRequestJoinCompany.getSenderId());
            if (!newRequestJoinCompany.getStatus().toLowerCase().equals("deny")) {
                recruiterService.updateCompany(newRequestJoinCompany.getCompanyId(), newRequestJoinCompany.getSenderId());
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "succesful", newRequestJoinCompany);
        } catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/get-recruiter-by-company")
    public ResponseData getRecruiterByCompany(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam("companyId") long companyId,
                                              @RequestParam(value = "fullName", defaultValue = StringUtils.EMPTY) String fullName,
                                              @RequestParam(value = "email", defaultValue = StringUtils.EMPTY) String email,
                                              @RequestParam(value = "phone", defaultValue = StringUtils.EMPTY) String phone) {
        try {
            ResponseDataPagination pagination = recruiterManageService.getRecruitersOfCompany
                    (pageNo, pageSize, companyId, fullName, email, phone);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @PutMapping("/remove-recruiter-from-company/{recruiterId}")
    @Operation(summary = "xóa recruiter khỏi company")
    public ResponseData removeRecruiterFromCompany(@PathVariable(value = "recruiterId") long recruiterId){
        try {
            recruiterService.removeRecruiterFromCompany(recruiterId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS);
        } catch (Exception e){
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("find-cv")
    public ResponseData findCv(@RequestParam(defaultValue = "0") Integer pageNo,
                               @RequestParam(defaultValue = "6") Integer pageSize,
                               @RequestParam(required = false) String experienceYear,
                               @RequestParam(required = false) String candidateAddress,
                               @RequestParam(required = false) String techStacks) {
        try {
            ResponseDataPagination pagination = recruiterManageService.
                    findCVFilter(pageNo, pageSize, experienceYear, candidateAddress, techStacks);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PutMapping("/upload-business-license")
    public ResponseData uploadBusinessLicense(@ModelAttribute UploadBusinessLicenseRequest request) {
        try {
            Recruiter recruiter = recruiterService.uploadLicense(request.getRecruiterId(), request.getBusinessMultipartFile(), request.getAdditionalMultipartFile());
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, recruiter);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/common-infor/{recruiterId}")
    @Operation(summary = "tên nhà tuyển dụng, đếm tổng số job đã tạo, và xác thực đến bước mấy, tỉ lệ ứng viên apply")
    public ResponseData getCommonInforOfRecruiter(@PathVariable("recruiterId") long recruiterId) {
        try {
            CommonRecruiterInformationResponse response = recruiterManageService.getCommonInforOfRecruiter(recruiterId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, response);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/get-business-license/{id}")
    public ResponseData getBusinessLicense(@PathVariable("id") long recruiterId) {
        try {
            Recruiter recruiter = recruiterService.getRecruiterById(recruiterId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, recruiter);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/total-view-cv/{id}")
    public ResponseData getTotalViewCV(@PathVariable long id) {
        try {
            Integer total = recruiterService.getTotalViewCV(id);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, total);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/get-receive-request")
    public ResponseData getReceiveRequestJoinCompanyWithFilter(
                                  @RequestParam long approverId,
                                  @RequestParam(defaultValue = "0") Integer pageNo,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(defaultValue = "") String fullName,
                                  @RequestParam(defaultValue = "") String email,
                                  @RequestParam(defaultValue = "") String status,
                                  @RequestParam(defaultValue = "") String phone) {
        try {
            Page<ReceiveRequestJoinCompanyResponse> page = requestJoinCompanyService.getReceiveRequestJoinCompanyWithFilter(fullName, email, phone, status, approverId, pageSize, pageNo);
            ResponseDataPagination responseDataPagination = new ResponseDataPagination();
            Pagination pagination = new Pagination();
            System.out.println(page.getContent());
            responseDataPagination.setData(page.getContent());
            pagination.setCurrentPage(pageNo);
            pagination.setPageSize(pageSize);
            pagination.setTotalPage(page.getTotalPages());
            pagination.setTotalRecords(Integer.parseInt(String.valueOf(page.getTotalElements())));
            responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
            responseDataPagination.setPagination(pagination);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "", responseDataPagination);
        } catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }
    @GetMapping("/save-or-unsave-cv")
    public ResponseData saveOrUnSaveCV(@RequestParam boolean isSave, @RequestParam long id, @RequestParam long recruiterId) {
        try{
            Optional<ProfileViewer> profileViewerOP = profileViewerService.getByCvIdAndViewerIdOptional(id, recruiterId);
            if(!profileViewerOP.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.PROFILE_VIEWER_NOT_FOUND);
            }
            profileViewerService.updateIsSave(isSave, id,recruiterId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, isSave);
        }catch (Exception ex){
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/is-seen-uploaded-cv")
    public ResponseData isSeenUploadedCV(@RequestParam long candidateId, @RequestParam long jobId) {
        try {
            Optional<AppliedJob> appliedJobOP = appliedJobService.getAppliedJobByJobIDandCandidateID(jobId, candidateId);
            if(!appliedJobOP.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), "Candidate is not applied this job");
            }
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "SUCCESS", appliedJobOP.get().isSeenUploadedCV());
        }catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/see-upload-cv-detail")
    public ResponseData seeUploadCVDetail(@RequestParam long candidateId, @RequestParam long jobId, @RequestParam long recruiterId) {
        try {
            Optional<AppliedJob> appliedJobOP = appliedJobService.getAppliedJobByJobIDandCandidateID(candidateId, jobId);
            if(!appliedJobOP.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), "Candidate is not applied this job");
            }
            if(appliedJobOP.get().isSeenUploadedCV()) {
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "CAN READ CV 1", appliedJobOP.get().getCvUploadUrl());
            }

            Optional<Recruiter> recruiterOptional = recruiterService.findById(recruiterId);
            if(!recruiterOptional.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.RECRUITER_DOES_NOT_EXIST);
            }
            if(recruiterOptional.get().getTotalCvView() == 0) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.TOTAL_VIEW_CV_RUN_OUT);
            }
            if(recruiterOptional.get().getTotalCvView() == -1) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.PAY_FOR_PACKAGE_TO_VIEW_CV);
            }
            recruiterService.updateTotalCvView(recruiterOptional.get().getTotalCvView() - 1, recruiterOptional.get().getId());
            appliedJobService.updateIsSeenUploadedCV(jobId, candidateId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "CAN READ CV", appliedJobOP.get().getCvUploadUrl());

        }catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

}
