package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.CV.CVRequest;
import fpt.edu.capstone.dto.CV.CVResponse;
import fpt.edu.capstone.dto.candidate.AppliedJobCandidateResponse;
import fpt.edu.capstone.dto.candidate.ProfileViewerResponse;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.job.AppliedJobRequest;
import fpt.edu.capstone.dto.job.JobResponse;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.AppliedJobRepository;
import fpt.edu.capstone.repository.CVRepository;
import fpt.edu.capstone.repository.EducationRepository;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.Pagination;
import fpt.edu.capstone.utils.ResponseDataPagination;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CandidateManageServiceImpl implements CandidateManageService {

    private final ModelMapper modelMapper;

    private final AppliedJobService appliedJobService;

    private final CandidateService candidateService;

    private final JobService jobService;

    private final CompanyService companyService;

    private final JobHashTagService jobHashTagService;

    private final CVService cvService;

    private final WorkExperienceService workExperienceService;

    private final MajorLevelService majorLevelService;

    private final OtherSkillService otherSkillService;

    private final LanguageService languageService;

    private final CertificateService certificateService;

    private final EducationService educationService;

    private final ProfileViewerService profileViewerService;

    private final AppliedJobRepository appliedJobRepository;

    private final ImageService imageService;

    private final RecruiterService recruiterService;

    @Override
    public ResponseDataPagination searchAppliedJobsOfCandidate(Integer pageNo, Integer pageSize, long candidateId, String approvalStatus) {
        if (!candidateService.existsById(candidateId)) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        List<AppliedJobCandidateResponse> responseList = new ArrayList<>();

        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);

        Page<AppliedJob> appliedJobs = appliedJobService.searchAppliedJobsOfCandidate(pageable, candidateId, approvalStatus);
        if (appliedJobs.hasContent()) {
            for (AppliedJob appliedJob : appliedJobs) {
                AppliedJobCandidateResponse response = new AppliedJobCandidateResponse();
                Job job = jobService.getJobById(appliedJob.getJobId());
                response.setAppliedJobId(appliedJob.getId());
                response.setJob(job);
                response.setApprovalStatus(appliedJob.getApprovalStatus());
                response.setAppliedJobDate(appliedJob.getCreatedAt());
                if (appliedJob.getApprovalStatus().equals(Enums.ApprovalStatus.APPROVED.getStatus())) {
                    response.setApprovalDate(appliedJob.getUpdatedAt());
                }
                Optional<Image> image = imageService.getImageCompany(job.getCompanyId(), true);
                Company company = companyService.getCompanyById(job.getCompanyId());
                if(image.isPresent()) {
                    response.setCompanyAvatar(image.get().getUrl());
                }
                response.setCompanyName(company.getName());
                responseList.add(response);
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(responseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(appliedJobs.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(appliedJobs.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    public ResponseDataPagination getJobsOfCompany(Integer pageNo, Integer pageSize, long companyId) {
        List<JobResponse> responseList = new ArrayList<>();
        Page<Job> jobs = jobService.getJobByCompanyId(pageNo, pageSize, companyId);
        if (jobs.hasContent()) {
            for (Job job : jobs) {
                JobResponse jobResponse = new JobResponse();
                jobResponse.setJobId(job.getId());
                jobResponse.setCompanyId(job.getCompanyId());
                jobResponse.setRecruiterId(job.getRecruiterId());
                List<JobHashtag> listJobHashTag = jobHashTagService.getHashTagOfJob(job.getId());
                if (!(listJobHashTag.isEmpty() && listJobHashTag == null)) {
                    List<String> hashTagNameList = listJobHashTag.stream().map(JobHashtag::getHashTagName).collect(Collectors.toList());
                    jobResponse.setListHashtag(hashTagNameList);
                }
                Company company = companyService.getCompanyById(job.getCompanyId());
                if (company != null) {
                    jobResponse.setCompanyName(company.getName());
                }
                Optional<Image> image = imageService.getImageCompany(companyId, true);
                jobResponse.setCompanyAvatar(image.get().getUrl());
                jobResponse.setJobName(job.getJobName());
                jobResponse.setJobDescription(job.getJobDescription());
                jobResponse.setJobRequirement(job.getJobRequirement());
                jobResponse.setBenefit(job.getBenefit());
                jobResponse.setFromSalary(job.getFromSalary());
                jobResponse.setToSalary(job.getToSalary());
                jobResponse.setNumberRecruits(job.getNumberRecruits());
                jobResponse.setRank(job.getRank());
                jobResponse.setWorkForm(job.getWorkForm());
                jobResponse.setGender(job.getGender());
                jobResponse.setStartDate(job.getStartDate());
                jobResponse.setEndDate(job.getEndDate());
                jobResponse.setWorkPlace(job.getWorkPlace());
                jobResponse.setCreatedAt(job.getCreatedAt());
                jobResponse.setUpdatedAt(job.getUpdatedAt());
                responseList.add(jobResponse);
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(responseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(jobs.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(jobs.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public CVResponse findCvByCandidateId(long candidateId) {
        List<CV> cv = cvService.findCvByCandidateId(candidateId);
        if (cv == null || cv.isEmpty()) {
            throw new HiveConnectException(ResponseMessageConstants.CV_NOT_EXIST);
        }
        List<Certificate> certificates = certificateService.getListCertificateByCvId(cv.get(0).getId());
        List<Education> educations = educationService.getListEducationByCvId(cv.get(0).getId());
        List<Language> languages = languageService.getListLanguageByCvId(cv.get(0).getId());
        List<MajorLevel> majorLevels = majorLevelService.getListMajorLevelByCvId(cv.get(0).getId());
        List<OtherSkill> otherSkills = otherSkillService.getListOtherSkillByCvId(cv.get(0).getId());
        List<WorkExperience> workExperiences = workExperienceService.getListWorkExperienceByCvId(cv.get(0).getId());
        CVResponse cvResponse = new CVResponse();
        cvResponse.setCandidateId(candidateId);
        cvResponse.setCreatedAt(cv.get(0).getCreatedAt());
        cvResponse.setUpdatedAt(cv.get(0).getUpdatedAt());
        cvResponse.setCertificates(certificates);
        cvResponse.setIsDeleted(cv.get(0).getIsDeleted());
        cvResponse.setEducations(educations);
        cvResponse.setId(cv.get(0).getId());
        cvResponse.setLanguages(languages);
        cvResponse.setSummary(cv.get(0).getSummary());
        cvResponse.setMajorLevels(majorLevels);
        cvResponse.setOtherSkills(otherSkills);
        cvResponse.setWorkExperiences(workExperiences);
        return cvResponse;
    }

    @Override
    public CV createCV(CVRequest request) {
        Candidate c = candidateService.getCandidateById(request.getCandidateId());
        if (c == null) {
            throw new HiveConnectException(ResponseMessageConstants.CANDIDATE_DOES_NOT_EXIST);
        }
        List<CV> cvList = cvService.findCvByCandidateId(request.getCandidateId());
        if (!cvList.isEmpty()) {
            throw new HiveConnectException(ResponseMessageConstants.YOUR_CV_EXISTED);
        }
        CV cv = modelMapper.map(request, CV.class);
        cv.create();
        cv.setIsDeleted(0);
        cvService.save(cv);
        return cv;
    }

    @Override
    public ResponseDataPagination getProfileViewer(Integer pageNo, Integer pageSize, long cvId, long candidateId) {
        List<ProfileViewerResponse> responseList = new ArrayList<>();
        Candidate candidate = candidateService.getCandidateById(candidateId);
        if (candidate == null) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        Optional<CV> cv = cvService.findByIdAndCandidateId(cvId, candidateId);
        if (!cv.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.CV_NOT_EXIST);
        }
        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);

        Page<ProfileViewer> profileViewersOfCv = profileViewerService.getProfileViewerOfCv(pageable, cvId);
        if (profileViewersOfCv.hasContent()) {
            for (ProfileViewer viewer : profileViewersOfCv) {
                ProfileViewerResponse response = new ProfileViewerResponse();
                Recruiter recruiter = recruiterService.getRecruiterById(viewer.getViewerId());
                if (recruiter != null) {
                    response.setViewerName(recruiter.getFullName());
                    response.setCompanyId(recruiter.getCompanyId());
                    Company company = companyService.getCompanyById(recruiter.getCompanyId());
                    if (company != null) {
                        response.setCompanyName(company.getName());
                    }
                    response.setViewDate(viewer.getCreatedAt());
                    Optional<Image> image = imageService.getImageCompany(company.getId(), true);
                    if (image != null) {
                        response.setCompanyAvatar(image.get().getUrl());
                    }
                }
                responseList.add(response);
            }
//            responseList = profileViewerService.findAll(PageRequest.of(pageReq, pageSize, Sort.by(Sort.Direction.DESC, "id")));
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(responseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(profileViewersOfCv.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(profileViewersOfCv.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public String appliedJob(AppliedJobRequest request) throws Exception {
        String message = "";
        if (request.getCvUrl() == null || request.getCvUrl().trim().isEmpty()) {
            //profile apply
            CV cv = cvService.getCVByCandidateId(request.getCandidateId());
            if (cv == null) {
                throw new HiveConnectException(ResponseMessageConstants.PROFILE_DOES_NOT_EXIST);
            }
        }
        if (!jobService.existsById(request.getJobId())) {
            throw new HiveConnectException(ResponseMessageConstants.JOB_DOES_NOT_EXIST);
        }
        if (!candidateService.existsById(request.getCandidateId())) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        //if exists candidate account, candidate has already applied the job
        AppliedJob appliedJob1 = appliedJobService.getAppliedJobBefore(request.getCandidateId(), request.getJobId());
        // từng apply, có record tồn tại
        if (appliedJob1 != null) {
            //đâ apply thì có thể hủy apply
            if (appliedJob1.isApplied()) {
                //đã apply và đang ở trạng thái pending hoặc approved, có thể hủy apply
                if (appliedJob1.getApprovalStatus().equals(Enums.ApprovalStatus.PENDING.getStatus())
                        || appliedJob1.getApprovalStatus().equals(Enums.ApprovalStatus.APPROVED.getStatus())) {
                    appliedJob1.setApplied(false);
                    appliedJob1.update();
                    message = ResponseMessageConstants.CANCEL_APPLY_SUCCESSFULLY;
                    //đã apply và ở trạng thái reject, có thể apply lại (new record)
                } else if (appliedJob1.getApprovalStatus().equals(Enums.ApprovalStatus.REJECT.getStatus())) {
                    createNewApplyRecord(request);
                    message = ResponseMessageConstants.APPLY_FOR_JOB_SUCCESSFULLY;
                }
            }else {//đã hủy apply với tin tuyển dụng này rồi, tạo một apply mới
                createNewApplyRecord(request);
                message = ResponseMessageConstants.APPLY_FOR_JOB_SUCCESSFULLY;
            }
        } else {//chưa apply job này lần nào
            createNewApplyRecord(request);
            message = ResponseMessageConstants.APPLY_FOR_JOB_SUCCESSFULLY;
        }
        return message;
    }

    private void createNewApplyRecord(AppliedJobRequest request) {
        AppliedJob appliedJob = new AppliedJob();
        appliedJob.setJobId(request.getJobId());
        appliedJob.setCandidateId(request.getCandidateId());
        appliedJob.setApplied(true);
        appliedJob.setApprovalStatus(Enums.ApprovalStatus.PENDING.getStatus());
        if (request.getCvUrl() != null && !request.getCvUrl().trim().isEmpty()) {
            appliedJob.setCvUploadUrl(request.getCvUrl());
            appliedJob.setUploadCv(true);
            appliedJob.setSeenUploadedCV(false);
        }
        appliedJob.create();
        appliedJobRepository.save(appliedJob);
    }
}
