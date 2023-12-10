package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.job.*;
import fpt.edu.capstone.dto.recruiter.CountTotalCreatedJobResponse;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
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

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@Service
@AllArgsConstructor
public class JobServiceImpl implements JobService {
    private final ModelMapper modelMapper;

    private final CompanyService companyService;

    private final RecruiterService recruiterService;

    private final CandidateService candidateService;

    private final JobRepository jobRepository;

    private final CVService cvService;

    private final MajorService majorService;

    private final MajorLevelService majorLevelService;

    private final FieldsService fieldsService;

    private final ImageService imageService;

    private final JobHashTagService jobHashTagService;

    private final AppliedJobService appliedJobService;

    @Override
    public void createJob(CreateJobRequest request) {
        if(!request.getFlag().equals(Enums.Flag.Posted.getStatus())
                && !request.getFlag().equals(Enums.Flag.Draft.getStatus())) {
            throw new HiveConnectException(ResponseMessageConstants.CREATE_JOB_STATUS_INVALID);
        }
        long companyId = request.getCompanyId();
        long recruiterId = request.getRecruiterId();
        long fieldId = request.getFieldId();
        if (!companyService.findById(companyId).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.COMPANY_DOES_NOT_EXIST);
        }
        if (!recruiterService.existById(recruiterId)) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        if (!fieldsService.existById(fieldId)) {
            throw new HiveConnectException(ResponseMessageConstants.FIELD_WORK_OF_COMPANY_DOES_NOT_EXIST);
        }
        if(request.getFromSalary() < 0 || request.getToSalary() < 0 || (request.getFromSalary() > request.getToSalary())) {
            throw new HiveConnectException(ResponseMessageConstants.SALARY_INVALID);
        }
        Job job = modelMapper.map(request, Job.class);
        job.create();
        job.setFlag(request.getFlag());
        jobRepository.save(job);
    }

    @Override
    public ResponseDataPagination searchListJobFilter(Integer pageNo, Integer pageSize, long fieldId,
                                                      long countryId, String jobName, String workForm,
                                                      String workPlace, long fromSalary, long toSalary) {
        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        Page<Job> jobs = jobRepository.searchListJobFilter(pageable, fieldId, countryId, jobName, workForm, workPlace, fromSalary, toSalary);
        List<JobResponse> jobResponse = new ArrayList<>();
        if (jobs.hasContent()) {
            for (Job j : jobs.getContent()) {
                if (!LocalDateTimeUtils.checkExpireTime(j.getEndDate())) {
                    JobResponse jr = modelMapper.map(j, JobResponse.class);
                    jr.setJobId(j.getId());
                    Company company = companyService.getCompanyById(j.getCompanyId());
                    if (company != null) {
                        jr.setCompanyName(company.getName());
                    }
                    jr.setCompanyName(company.getName());
                    Optional<Image> image = imageService.getImageCompany(company.getId(), true);
                    if(image.isPresent()){
                        jr.setCompanyAvatar(image.get().getUrl());
                    }
                    jobResponse.add(jr);
                }
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(jobResponse);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(jobs.getTotalPages());
        pagination.setTotalRecords(jobResponse.size());
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);

        return responseDataPagination;
    }

    @Override
    //TODO : fix insert for update function
    public void updateJob(UpdateJobRequest request) {
        if(!request.getFlag().equals(Enums.Flag.Posted.getStatus())
                && !request.getFlag().equals(Enums.Flag.Draft.getStatus())) {
            throw new HiveConnectException(ResponseMessageConstants.CREATE_JOB_STATUS_INVALID);
        }
        Job job = jobRepository.getById(request.getJobId());
        if (job == null) {
            throw new HiveConnectException(ResponseMessageConstants.JOB_DOES_NOT_EXIST);
        }
        int countApplied = appliedJobService.countAppliedCVOfJob(request.getJobId());
        if(countApplied > 1){
            throw new HiveConnectException(ResponseMessageConstants.JOB_HAS_AN_APPLIED);
        }

        job = modelMapper.map(request, Job.class);
        job.setStartDate(request.getStartDate());
        job.setEndDate(request.getEndDate());
        job.setId(request.getJobId());
        job.setCreatedAt(request.getCreatedAt());
        job.create();
//        job.setFlag(Enums.Flag.Posted.getStatus());
        job.setFlag(request.getFlag());
        jobRepository.saveAndFlush(job);
    }

    @Override
    public void saveJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public void deleteJob(long jobId) {
        Job job = jobRepository.getById(jobId);
        if (job == null) {
            throw new HiveConnectException(ResponseMessageConstants.JOB_DOES_NOT_EXIST);
        }
        job.setIsDeleted(1);
        jobRepository.save(job);
    }

    @Override
    public boolean existsById(long id) {
        return jobRepository.existsById(id);
    }

    @Override
    public Page<Job> getNewestJobList(Pageable pageable) {
        String flag = Enums.Flag.Posted.getStatus();
        return jobRepository.getNewestJob(pageable, true, 0, flag);
    }

    @Override
    public Page<Job> getUrgentJobList(Pageable pageable) {
        String flag = Enums.Flag.Posted.getStatus();
        return jobRepository.getUrgentJob(pageable, true, 0, flag);
    }

    @Override
    public Page<Job> getPopularJobList(Pageable pageable, List<Long> popularJobId) {
        String flag = Enums.Flag.Posted.getStatus();
        return jobRepository.getPopularJob(pageable, true, 0, flag, popularJobId);
    }

    @Override
    public Page<Job> getJobOfRecruiter(Pageable pageable, long recruiterId, String jobName) {
        return jobRepository.getAllByRecruiterIdAndJobNameOrderByUpdatedAtDesc(pageable, recruiterId, jobName);
    }

    @Override
    public Optional<Job> findById(long id) {
        return jobRepository.findById(id);
    }


    @Override
    public Page<Job> getJobByCompanyId(long pageNo, long quantity, long companyId) {
        int pageReq = (int) (pageNo >= 1 ? pageNo - 1 : pageNo);
        Pageable pageable = PageRequest.of(pageReq, (int) quantity);
        return jobRepository.getJobByCompanyId(companyId, pageable);
    }

    @Override
    public List<Job> getJobByRecruiterId(long recId) {
        return jobRepository.findAllByRecruiterId(recId);
    }

    @Override
    public List<JobResponse> getSameJobsOtherCompanies(long detailJobId) {
        List<Job> jobs = jobRepository.getSameJobsOtherCompanies(detailJobId);
        List<JobResponse> responseList = new ArrayList<>();
        for (Job job : jobs) {
            if (!LocalDateTimeUtils.checkExpireTime(job.getEndDate())) {
                JobResponse jobResponse = modelMapper.map(job, JobResponse.class);
                Optional<Image> image = imageService.getImageCompany(job.getCompanyId(), true);
                jobResponse.setJobId(job.getId());
                if(image.isPresent()){
                    jobResponse.setCompanyAvatar(image.get().getUrl());
                }
                Company company = companyService.getCompanyById(job.getCompanyId());
                if (company != null) {
                    jobResponse.setCompanyName(company.getName());
                }
                responseList.add(jobResponse);
            }
        }
        return responseList;
    }

    @Override
    public HomePageData getDataHomePage() {
        HomePageData data = new HomePageData();
        int pageReq = 0 >= 1 ? 0 - 1 : 0;
        Pageable pageable = PageRequest.of(pageReq, 10);
        String flag = Enums.Flag.Posted.getStatus();
        Page<Job> fullTimeList = jobRepository.getListJobByWorkForm(pageable, "FULLTIME", flag);
        Page<Job> partTimeList = jobRepository.getListJobByWorkForm(pageable, "PARTTIME", flag);
        Page<Job> remoteList = jobRepository.getListJobByWorkForm(pageable, "REMOTE", flag);
        Page<Job> popularList = jobRepository.getPopularJob(pageable, true, 0, flag,null);
        Page<Job> newestList = jobRepository.getNewestJob(pageable, true, 0, flag);
        Page<Job> urgentList = jobRepository.getUrgentJob(pageable, true, 0, flag);
        Page<Job> jobByFields = jobRepository.getListJobByFieldId(pageable, 1, flag);
        data.setFulltimeJob(displayJobInHomePage(fullTimeList));
        data.setParttimeJob(displayJobInHomePage(partTimeList));
        data.setRemoteJob(displayJobInHomePage(remoteList));
        data.setPopularJob(displayJobInHomePage(popularList));
        data.setNewJob(displayJobInHomePage(newestList));
        data.setUrgentJob(displayJobInHomePage(urgentList));
        data.setJobByFields(displayJobInHomePage(jobByFields));
        return data;
    }

    @Override
    public int countJobInSystem() {
        String flag = Enums.Flag.Posted.getStatus();
        return jobRepository.countJobInSystem(flag);
    }

    @Override
    public void draftJob(long jobId) {
        Job job = jobRepository.getById(jobId);
        int countApplied = appliedJobService.countAppliedCVOfJob(jobId);
        if(countApplied > 1){
            throw new HiveConnectException(ResponseMessageConstants.JOB_HAS_AN_APPLIED_2);
        }
        job.setFlag(Enums.Flag.Draft.getStatus());
        jobRepository.saveAndFlush(job);

    }

    @Override
    public List<Long> getListPopularJobId() {
        //Lấy ra những job phổ biến với điều kiện có từ 2 lượt apply
        return jobRepository.getListPopularJobId(1);
    }

    @Override
    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    public List<JobHomePageResponse> displayJobInHomePage(Page<Job> jobs) {
        List<JobHomePageResponse> responseList = new ArrayList<>();
        if (jobs.hasContent()) {
            for (Job job : jobs) {
                JobHomePageResponse jobResponse = modelMapper.map(job, JobHomePageResponse.class);
                List<JobHashtag> listJobHashTag = jobHashTagService.getHashTagOfJob(job.getId());
                if (!(listJobHashTag.isEmpty() && listJobHashTag == null)) {
                    List<String> hashTagNameList = listJobHashTag.stream().map(JobHashtag::getHashTagName).collect(Collectors.toList());
                    jobResponse.setListHashtag(hashTagNameList);
                }
                Company company = companyService.getCompanyById(job.getCompanyId());
                if (company != null) {
                    jobResponse.setCompanyName(company.getName());
                }
                Optional<Image> image = imageService.getImageCompany(job.getCompanyId(), true);
                if(image.isPresent()) {
                    jobResponse.setCompanyAvatar(image.get().getUrl());
                }
                responseList.add(jobResponse);
            }
        }
        return responseList;
    }

    @Override
    public Job getJobById(long jobId) {
        Job job = jobRepository.getById(jobId);
        if (job == null || job.getId() == 0) {
            throw new HiveConnectException(ResponseMessageConstants.JOB_DOES_NOT_EXIST);
        }
        return job;
    }

    @Override
    public ResponseDataPagination getJobByFieldId(Integer pageNo, Integer pageSize, long id) {
        List<JobResponse> responseList = new ArrayList<>();

        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        String flag = Enums.Flag.Posted.getStatus();
        Page<Job> jobs = jobRepository.getListJobByFieldId(pageable, id, flag);
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
                    Optional<Image> image = imageService.getImageCompany(job.getCompanyId(), true);
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
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(jobs.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public List<JobResponse> getListSuggestJobByCv(long candidateId) {
        if (!candidateService.existsById(candidateId)) {
            throw new HiveConnectException(ResponseMessageConstants.CANDIDATE_DOES_NOT_EXIST);
        }
        CV cv = cvService.getCVByCandidateId(candidateId);
        List<MajorLevel> majorLevel = majorLevelService.getListMajorLevelByCvId(cv.getId()); // lấy ra major của candidate để filter
        List<JobResponse> responses = new ArrayList<>();
        for (MajorLevel ml : majorLevel) {
            String majorName = majorService.getNameByMajorId(ml.getMajorId()).toLowerCase().trim(); //C#
            List<Job> jobList = jobRepository.getListSuggestJobByCv(majorName);
            for (Job j : jobList) {
                if (!LocalDateTimeUtils.checkExpireTime(j.getEndDate())) {
                    JobResponse jr = modelMapper.map(j, JobResponse.class);
                    Optional<Image> image = imageService.getImageCompany(jr.getCompanyId(), true);
                    jr.setJobId(j.getId());
                    jr.setCompanyAvatar(image.get().getUrl());
                    responses.add(jr);
                }
            }
        }
        return responses;
    }

    @Override
    public CountTotalCreatedJobResponse countTotalCreatedJobOfRecruiter(long recruiterId) {
        return jobRepository.countTotalCreatedJobOfRecruiter(recruiterId);
    }
}
