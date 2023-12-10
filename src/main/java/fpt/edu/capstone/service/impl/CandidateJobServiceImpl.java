package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.job.ApprovalJobRequest;
import fpt.edu.capstone.dto.job.JobDetailResponse;
import fpt.edu.capstone.dto.job.JobResponse;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.AppliedJobRepository;
import fpt.edu.capstone.repository.FollowRepository;
import fpt.edu.capstone.repository.JobRepository;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.LocalDateTimeUtils;
import fpt.edu.capstone.utils.Pagination;
import fpt.edu.capstone.utils.ResponseDataPagination;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CandidateJobServiceImpl implements CandidateJobService {

    private final ModelMapper modelMapper;

    private final JobService jobService;

    private final JobRepository jobRepository;

    private final JobHashTagService jobHashTagService;

    private final CompanyService companyService;

    private final AppliedJobService appliedJobService;

    private final AppliedJobRepository appliedJobRepository;

    private final FieldsService fieldsService;

    private final ImageService imageService;

    private final FollowRepository followRepository;

    private final PaymentService paymentService;

    @Override
    public ResponseDataPagination getNewestJob(Integer pageNo, Integer pageSize) {
        List<JobResponse> responseList = new ArrayList<>();

        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);

        Page<Job> jobs = jobService.getNewestJobList(pageable);
        if (jobs.hasContent()) {
            for (Job job : jobs) {
                if (!LocalDateTimeUtils.checkExpireTime(job.getEndDate())) {
                    JobResponse jobResponse = new JobResponse();
                    Optional<Payment> payment = paymentService.findByJobId(job.getId());
                    if (payment == null || !payment.isPresent()) {
                        jobResponse.setPayment(false);
                    } else {
                        jobResponse.setPayment(true);
                    }
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

                    Optional<Image> image = imageService.getImageCompany(company.getId(), true);
                    if (image.isPresent()) {
                        jobResponse.setCompanyAvatar(image.get().getUrl());
                    }
                    responseList.add(jobResponse);
                }
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(responseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(jobs.getTotalPages());
        pagination.setTotalRecords(responseList.size());
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public ResponseDataPagination getUrgentJob(Integer pageNo, Integer pageSize) {
        List<JobResponse> responseList = new ArrayList<>();

        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);

        Page<Job> jobs = jobService.getUrgentJobList(pageable);
        if (jobs.hasContent()) {
            for (Job job : jobs) {
                if (!LocalDateTimeUtils.checkExpireTime(job.getEndDate())) {
                    JobResponse jobResponse = new JobResponse();
                    Optional<Payment> payment = paymentService.findByJobId(job.getId());
                    if (payment == null || !payment.isPresent()) {
                        jobResponse.setPayment(false);
                    } else {
                        jobResponse.setPayment(true);
                    }
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

                    Optional<Image> image = imageService.getImageCompany(company.getId(), true);
                    if (image.isPresent()) {
                        jobResponse.setCompanyAvatar(image.get().getUrl());
                    }
                    responseList.add(jobResponse);
                }
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(responseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(jobs.getTotalPages());
        pagination.setTotalRecords(responseList.size());
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public ResponseDataPagination getListJobByWorkForm(Integer pageNo, Integer pageSize, String workForm) {
        List<JobResponse> responseList = new ArrayList<>();

        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        String flag = Enums.Flag.Posted.getStatus();
        Page <Job> jobs = jobRepository.getListJobByWorkForm(pageable, workForm,flag);

        if (jobs.hasContent()) {
            for (Job job : jobs) {
                if (!LocalDateTimeUtils.checkExpireTime(job.getEndDate())) {
                    JobResponse jobResponse = modelMapper.map(job, JobResponse.class);
                    jobResponse.setJobId(job.getId());
                    List<JobHashtag> listJobHashTag = jobHashTagService.getHashTagOfJob(job.getId());
                    if (!(listJobHashTag.isEmpty() && listJobHashTag == null)) {
                        List<String> hashTagNameList = listJobHashTag.stream().map(JobHashtag::getHashTagName).collect(Collectors.toList());
                        jobResponse.setListHashtag(hashTagNameList);
                    }
                    Company company = companyService.getCompanyById(job.getCompanyId());
                    if (company != null) {
                        jobResponse.setCompanyName(company.getName());
                    }
                    Optional<Image> image = imageService.getImageCompany(company.getId(), true);
                    if (image.isPresent()) {
                        jobResponse.setCompanyAvatar(image.get().getUrl());
                    }
                    responseList.add(jobResponse);
                }
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(responseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(jobs.getTotalPages());
        pagination.setTotalRecords(responseList.size());
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public ResponseDataPagination getPopularJob(Integer pageNo, Integer pageSize) {
        List<JobResponse> responseList = new ArrayList<>();

        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        List <Long> popularJobId = jobService.getListPopularJobId();
        Page<Job> jobs = jobService.getPopularJobList(pageable, popularJobId);
        if (jobs.hasContent()) {
            for (Job job : jobs) {
                JobResponse jobResponse = new JobResponse();
                if (!LocalDateTimeUtils.checkExpireTime(job.getEndDate())) {
                    Optional<Payment> payment = paymentService.findByJobId(job.getId());
                    if (payment == null || !payment.isPresent()) {
                        jobResponse.setPayment(false);
                    } else {
                        jobResponse.setPayment(true);
                    }
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

                    Optional<Image> image = imageService.getImageCompany(company.getId(), true);
                    if (image.isPresent()) {
                        jobResponse.setCompanyAvatar(image.get().getUrl());
                    }
                    responseList.add(jobResponse);
                }
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(responseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(jobs.getTotalPages());
        pagination.setTotalRecords(responseList.size());
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public void approveJob(ApprovalJobRequest request) {
        AppliedJob appliedJob = appliedJobService.getAppliedJobPendingApproval(request.getJobId(), request.getCandidateId());
        if (appliedJob == null) {
            throw new HiveConnectException("CV không tồn tại");
        }
        if (appliedJob.getApprovalStatus().equals(Enums.ApprovalStatus.PENDING.getStatus()) &&
                request.getApprovalStatus().equals("Approved")) {
            appliedJob.setApprovalStatus(Enums.ApprovalStatus.APPROVED.getStatus());
        } else if (appliedJob.getApprovalStatus().equals(Enums.ApprovalStatus.PENDING.getStatus()) &&
                request.getApprovalStatus().equals("Reject")) {
            appliedJob.setApprovalStatus(Enums.ApprovalStatus.REJECT.getStatus());
        }
        appliedJob.update();
        appliedJobRepository.save(appliedJob);
    }

    @Override
    public JobDetailResponse getJobDetail(long jobId, long candidateId) {
        JobDetailResponse detail = new JobDetailResponse();
        Job job = jobService.getJobById(jobId);
        //company information
        //company does not exist by user can post job -> exception
        if(String.valueOf(job.getCompanyId()) == null || job.getCompanyId() == 0) {
            throw new HiveConnectException(ResponseMessageConstants.PLEASE_TRY_TO_CONTACT_ADMIN);
        }
        Company company = companyService.getCompanyById(job.getCompanyId());
        //company does not exist by user can post job -> exception
        if (company == null) {
            throw new HiveConnectException(ResponseMessageConstants.PLEASE_TRY_TO_CONTACT_ADMIN);
        }
        detail.setCompanyName(company.getName());
        detail.setCompany(company);

        Fields fields = fieldsService.getById(job.getFieldId());
        detail.setJobId(jobId);
        detail.setRecruiterId(job.getRecruiterId());
        detail.setJobName(job.getJobName());
        detail.setFromSalary(job.getFromSalary());
        detail.setToSalary(job.getToSalary());
        detail.setNumberRecruits(job.getNumberRecruits());
        detail.setWorkForm(job.getWorkForm());
        detail.setRank(job.getRank());
        detail.setAcademicLevel(job.getAcademicLevel());
        detail.setGender(job.getGender());
        detail.setExperience(job.getExperience());
        detail.setWorkPlace(job.getWorkPlace());
        detail.setJobDescription(job.getJobDescription());
        detail.setJobRequirement(job.getJobRequirement());
        detail.setStartDate(job.getStartDate());
        detail.setEndDate(job.getEndDate());
        detail.setBenefit(job.getBenefit());
        detail.setFieldId(job.getFieldId());
        detail.setFieldName(fields.getFieldName());
        detail.setCreatedAt(job.getCreatedAt());
        detail.setUpdatedAt(job.getUpdatedAt());
        detail.setIsDeleted(job.getIsDeleted());
        detail.setWeekday(job.getWeekday());
        detail.setCountryId(job.getCountryId());
        detail.setCompanyId(job.getCompanyId());
        //candidate information
        detail.setCandidateId(candidateId);
        AppliedJob appliedJob = appliedJobService.getAppliedJobBefore(candidateId, jobId);
        if(appliedJob != null) {
            detail.setApplied(appliedJob.isApplied());
            detail.setApprovalStatus(appliedJob.getApprovalStatus());
        }
        Optional<Follow> followJob = followRepository.getFollowIfHave(candidateId, jobId, 1);
        if(followJob.isPresent()) {
            detail.setFollowing(true);
        } else {
            detail.setFollowing(false);
        }
        return detail;
    }
}
