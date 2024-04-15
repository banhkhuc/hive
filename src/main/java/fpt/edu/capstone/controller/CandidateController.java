package fpt.edu.capstone.controller;

import fpt.edu.capstone.dto.CV.ViewCvResponse;
import fpt.edu.capstone.dto.candidate.CVBaseInformationRequest;
import fpt.edu.capstone.dto.candidate.CandidateBaseInformationResponse;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.service.impl.CVImportedService;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.LogUtils;
import fpt.edu.capstone.utils.ResponseData;
import fpt.edu.capstone.utils.ResponseDataPagination;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/candidate")
@AllArgsConstructor
public class CandidateController {
    private static final Logger logger = LoggerFactory.getLogger(Candidate.class);

    private final CandidateService candidateService;

    private CVImportedService cvImportedService;

    private final ProfileViewerService profileViewerService;

    private final FollowService followService;

    private final JobService jobService;

    private final CompanyService companyService;

    private final RecruiterService recruiterService;

    private final CandidateManageService candidateManageService;

    private final UserService userService;

    private final RecruiterManageService recruiterManageService;

    @GetMapping("/all")
    public ResponseData getAllCandidate() {
        try {
            List<Candidate> listAllCandidate = candidateService.getAllCandidate();
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS,
                    listAllCandidate);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/find-by-userid")
    public ResponseData getCandidateById(@RequestParam long userId) {
        try {
            Optional<Candidate> candidate = candidateService.findCandidateByUserId(userId);
            Optional<Users> user = Optional.ofNullable(userService.findById(userId));
            if (candidate.isPresent() && user.isPresent()) {
                CandidateBaseInformationResponse response = new CandidateBaseInformationResponse();
                Candidate candidateNN = candidate.get();
                response.setAddress(candidateNN.getAddress());
                response.setAvatarUrl(candidateNN.getAvatarUrl());
                response.setCountry(candidateNN.getCountry());
                response.setBirthDate(candidateNN.getBirthDate());
                response.setId(candidateNN.getId());
                response.setExperienceLevel(candidateNN.getExperienceLevel());
                response.setFullName(candidateNN.getFullName());
                response.setGender(candidateNN.isGender());
                response.setIntroduction(candidateNN.getIntroduction());
                response.setNeedJob(candidateNN.isNeedJob());
                response.setSocialLink(candidateNN.getSocialLink());
                response.setUserId(candidateNN.getUserId());
                response.setPhoneNumber(user.get().getPhone());
                response.setVerifiedPhone(user.get().isVerifiedPhone());
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS,
                        response);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Can not find candidate by this user id", null);
        } catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseData updateCandidate(@RequestBody Candidate newCandidate, @PathVariable long id) {
        Optional<Candidate> foundedCandidate = candidateService.findById(id);
        if (!foundedCandidate.isPresent()) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        try {
            candidateService.updateCandidate(newCandidate, id);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Sửa thông tin ứng viên thành công", newCandidate);
        } catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @PutMapping("/update-candidate-base-information")
    public ResponseData updateCandidateBaseInformation(@RequestBody Candidate updateCandidate) {
        try {
            Optional<Candidate> candidate = candidateService.findById(updateCandidate.getId());
            if (candidate.isPresent()) {
                candidateService.updateCandidateInformation(updateCandidate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.UPDATE_SUCCESSFULLY, updateCandidate);
            }
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.USER_DOES_NOT_EXIST,
                    updateCandidate.getId());
        } catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }

    }

    @PutMapping("/update-cv-base-information")
    public ResponseData updateCvBaseInformation(@RequestBody CVBaseInformationRequest cvBaseInformationRequest) {
        try {
            Optional<Candidate> candidate = candidateService.findCandidateByUserId(cvBaseInformationRequest.getUserId());
            Optional<Users> users = Optional.ofNullable(userService.getUserById(cvBaseInformationRequest.getUserId()));

            CVBaseInformationRequest response = new CVBaseInformationRequest();
            if (candidate.isPresent() && users.isPresent()) {
                candidateService.updateCVInformation(cvBaseInformationRequest);
                if (userService.findByPhoneAndIdIsNotIn(cvBaseInformationRequest.getPhoneNumber(), cvBaseInformationRequest.getUserId()) != null) {
                    return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), "Số điện thoại đã được sử dụng");
                } else {
                    userService.updatePhoneNumber(cvBaseInformationRequest.getPhoneNumber(), cvBaseInformationRequest.getUserId());
                }

                if (users.get().isVerifiedPhone()) {
                    if (!cvBaseInformationRequest.getPhoneNumber().equals(userService.findByPhoneNumber(users.get().getPhone()))) {
                        return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), "Số điện thoại đã được xác minh! Không thể thay đổi");
                    }
                }
                Users usersNew = userService.getUserById(cvBaseInformationRequest.getUserId());
                Candidate candidateNewInfor = candidateService.findCandidateByUserId(cvBaseInformationRequest.getUserId()).get();

                response.setAddress(candidateNewInfor.getAddress());
                response.setName(candidateNewInfor.getFullName());
                response.setCountry(candidateNewInfor.getCountry());
                response.setBirthDate(candidateNewInfor.getBirthDate());
                response.setSocialLink(candidateNewInfor.getSocialLink());
                response.setUserId(candidateNewInfor.getUserId());
                response.setPhoneNumber(usersNew.getPhone());
                response.setGender(candidateNewInfor.isGender());
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.UPDATE_SUCCESSFULLY, response);

        } catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage(), null);
        }

    }

    @PutMapping("/update-is-need-job")
    public ResponseData updateIsNeedJob(@RequestParam long candidateId) {
        try {
            Optional<Candidate> candidate = candidateService.findById(candidateId);
            if (candidate.isPresent()) {
                candidateService.updateIsNeedJob(!candidate.get().isNeedJob(), candidate.get().getId());
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.UPDATE_SUCCESSFULLY, candidate.get().isNeedJob());
            }
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(),
                    ResponseMessageConstants.USER_DOES_NOT_EXIST, candidateId);
        } catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @PostMapping("upload-cv")
    public ResponseData uploadCV(@RequestParam("file") MultipartFile file, long candidateId) {
        try {
            Optional<Candidate> candidate = candidateService.findById(candidateId);
            if (candidate.isPresent()) { //Check if this user is candidate
                CVImported cvImported = cvImportedService.save(file, "CV", candidateId);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, cvImported.getId());
            } else {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.USER_DOES_NOT_EXIST, candidateId);
            }
        } catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/cv/{id}")
    public ResponseEntity<byte[]> getCVImported(@PathVariable String id) {
        Optional<CVImported> CVImported = cvImportedService.findById(id);

        if (!CVImported.isPresent()) {
            return ResponseEntity.notFound()
                    .build();
        }

        CVImported cvImported = CVImported.get();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + cvImported.getName() + "\"")
                .contentType(MediaType.valueOf(cvImported.getContentType()))
                .body(cvImported.getData());
    }

    @PostMapping("/insert-who-view-cv")
    @Operation(summary = "Insert when viewer click view CV of a candidate")
    public ResponseData insertWhoViewCv(@RequestBody ViewCvResponse response) {
        try {
            recruiterManageService.insertWhoViewCv(response);
            ProfileViewer profileViewer = profileViewerService.getByCvIdAndViewerId(response.getCvId(), response.getViewerId());
            if (profileViewer == null) {
                throw new HiveConnectException("Lưu người xem CV thất bại");
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, profileViewer);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/get-cv-viewer")
    @Operation(summary = "Get list viewer who view CV of a candidate")
    public ResponseData getRecruitersViewedCV(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam long cvId,
                                              @RequestParam long candidateId) {
        try {
            ResponseDataPagination pagination = candidateManageService
                    .getProfileViewer(pageNo, pageSize, cvId, candidateId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/follow-something")
    @Operation(summary = "Follow job, company, recruiter with type 1,2,3")
    public ResponseData follow(@RequestBody Follow follow) {
        try {
            if (followService.isFollowing(follow.getFollowerId(), follow.getFollowedId(), follow.getType())) {
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Đã theo dõi.");
            }
            if (follow.getType() != 1 && follow.getType() != 2 && follow.getType() != 3) {
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Vui lòng nhật đúng loại.");
            }
            if (!candidateService.findById(follow.getFollowerId()).isPresent()) {
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.USER_DOES_NOT_EXIST, follow.getFollowerId());
            }
            if (follow.getType() == 1) { //job
                if (!jobService.existsById(follow.getFollowedId())) {
                    return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.JOB_DOES_NOT_EXIST, follow.getFollowedId());
                }
            }
            if (follow.getType() == 2) { //company
                if (!companyService.existById(follow.getFollowedId())) {
                    return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.COMPANY_DOES_NOT_EXIST, follow.getFollowedId());
                }
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Successful but haven't handled this case", follow.getType());
            }
            if (follow.getType() == 3) { //Recruiter
                if (!recruiterService.existById(follow.getFollowedId())) {
                    return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.RECRUITER_DOES_NOT_EXIST, follow.getFollowedId());
                }
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Successful but haven't handled this case", follow.getType());
            }
            Follow insertedFollow = followService.insertFollow(follow);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Theo dõi thành công.", insertedFollow);
        } catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/followed-job")
    @Operation(summary = "Get list followed job with candidate id")
    public ResponseData getListFollowedJob(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestParam long candidateId) {
        try {
            ResponseDataPagination pagination = followService.getFollowedJobByCandidateID(pageNo, pageSize, candidateId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @DeleteMapping("/unfollow")
    @Operation(summary = "Unfollow something, must have correct type (1,2,3) ~ (job, company, recruiter")
    public ResponseData unfollow(@RequestParam long followerId, @RequestParam long followedId, @RequestParam long type) {
        try {
            if (!followService.isFollowing(followerId, followedId, type)) {
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "You have not followed this");
            }
            followService.unFollow(followerId, followedId, type);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Bỏ theo dõi thành công");
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/is-following")
    @Operation(summary = "Check if a is following b by a id type = 1,2,3 than b = job, company, recruiter")
    public ResponseData isFollowing(@RequestParam long followerId, @RequestParam long followedId, @RequestParam long type) {
        try {
            if (!followService.isFollowing(followerId, followedId, type)) {
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Not following", false);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Is following", true);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/get-applied-jobs-of-candidate")
    @Operation(summary = "View list applied job and approval status of a Candidate")
    public ResponseData searchListAppliedJob(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam long candidateId,
                                             @RequestParam(required = false, defaultValue = StringUtils.EMPTY) String approvalStatus) {
        try {
            ResponseDataPagination pagination = candidateManageService.
                    searchAppliedJobsOfCandidate(pageNo, pageSize, candidateId, approvalStatus);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }


}

