package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.job.ApprovalJobRequest;
import fpt.edu.capstone.dto.job.JobDetailResponse;
import fpt.edu.capstone.dto.job.JobResponse;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.AppliedJobRepository;
import fpt.edu.capstone.repository.FollowRepository;
import fpt.edu.capstone.repository.JobRepository;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CandidateJobServiceImplTest {

    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private JobService mockJobService;
    @Mock
    private JobRepository mockJobRepository;
    @Mock
    private JobHashTagService mockJobHashTagService;
    @Mock
    private CompanyService mockCompanyService;
    @Mock
    private AppliedJobService mockAppliedJobService;
    @Mock
    private AppliedJobRepository mockAppliedJobRepository;
    @Mock
    private FieldsService mockFieldsService;
    @Mock
    private ImageService mockImageService;
    @Mock
    private FollowRepository mockFollowRepository;
    @Mock
    private PaymentService paymentService;

    private CandidateJobServiceImpl candidateJobServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        candidateJobServiceImplUnderTest = new CandidateJobServiceImpl(mockModelMapper, mockJobService,
                mockJobRepository, mockJobHashTagService, mockCompanyService, mockAppliedJobService,
                mockAppliedJobRepository, mockFieldsService, mockImageService, mockFollowRepository, paymentService);
    }
    private Job job(){
        Job job = new Job(1L, 1L, "jobName", "workPlace", "workForm", LocalDateTime.now(),
                LocalDateTime.now(), 1L, 1L, 1L, "rank", "experience", false, "jobDescription",
                "jobRequirement", "benefit", 1L, 0, 1L, "weekday", 1L, "Posted", "academicLevel");
        return job;
    }

    private Company company(){
        Company company = new Company();
        company.setId(1L);
        company.setFieldWork("fieldWork");
        company.setName("name");
        company.setEmail("companyEmail");
        company.setPhone("companyPhone");
        company.setDescription("companyDescription");
        company.setWebsite("companyWebsite");
        company.setNumberEmployees("numberEmployees");
        company.setAddress("address");
        company.setTaxCode("taxCode");
        company.setIsDeleted(0);
        company.setMapUrl("mapUrl");
        company.setCreatorId(1L);
        company.setLocked(false);
        return company;
    }

    private Image imageEntity = new Image(1L, "name", "url", 1L,
            0, false, "contentType", "content".getBytes(), false);
    @Test
    public void testGetNewestJob() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getNewestJobList(any(Pageable.class))).thenReturn(jobs);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getNewestJob(1, 10);
    }

    @Test
    public void testGetNewestJob_JobServiceReturnsNoItems() {
        when(mockJobService.getNewestJobList(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getNewestJob(1, 10);
    }

    @Test
    public void testGetNewestJob_JobHashTagServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getNewestJobList(any(Pageable.class))).thenReturn(jobs);

        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(null);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);

        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getNewestJob(1, 10);
    }

    @Test
    public void testGetNewestJob_JobHashTagServiceReturnsNoItems() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getNewestJobList(any(Pageable.class))).thenReturn(jobs);

        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(Collections.emptyList());
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getNewestJob(1, 10);
    }

    @Test
    public void testGetNewestJob_CompanyServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getNewestJobList(any(Pageable.class))).thenReturn(jobs);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);

        when(mockCompanyService.getCompanyById(1L)).thenReturn(null);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getNewestJob(1, 10);
    }

    @Test
    public void testGetNewestJob_ImageServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getNewestJobList(any(Pageable.class))).thenReturn(jobs);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(null);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getNewestJob(1, 10);
    }

    @Test
    public void testGetUrgentJob() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getUrgentJobList(any(Pageable.class))).thenReturn(jobs);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getUrgentJob(1, 10);
    }

    @Test
    public void testGetUrgentJob_JobServiceReturnsNoItems() {
        when(mockJobService.getUrgentJobList(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getUrgentJob(1, 10);
    }

    @Test
    public void testGetUrgentJob_JobHashTagServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getUrgentJobList(any(Pageable.class))).thenReturn(jobs);

        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(null);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getUrgentJob(1, 10);
    }

    @Test
    public void testGetUrgentJob_JobHashTagServiceReturnsNoItems() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getUrgentJobList(any(Pageable.class))).thenReturn(jobs);

        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(Collections.emptyList());
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getUrgentJob(1, 10);
    }

    @Test
    public void testGetUrgentJob_CompanyServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getUrgentJobList(any(Pageable.class))).thenReturn(jobs);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        when(mockCompanyService.getCompanyById(1L)).thenReturn(null);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getUrgentJob(1, 10);
    }

    @Test
    public void testGetUrgentJob_ImageServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getUrgentJobList(any(Pageable.class))).thenReturn(jobs);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(null);

        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getUrgentJob(1, 10);

    }
    private JobResponse jobResponse(){
        JobResponse jobResponse = new JobResponse();
        jobResponse.setJobId(1L);
        jobResponse.setCompanyId(1L);
        jobResponse.setRecruiterId(1L);
        jobResponse.setListHashtag(Arrays.asList("value"));
        jobResponse.setCompanyName("companyName");
        jobResponse.setJobName("jobName");
        jobResponse.setJobDescription("jobDescription");
        jobResponse.setJobRequirement("jobRequirement");
        jobResponse.setBenefit("benefit");
        jobResponse.setFromSalary(1L);
        jobResponse.setToSalary(1L);
        jobResponse.setNumberRecruits(1L);
        jobResponse.setRank("rank");
        jobResponse.setWorkForm("workForm");
        jobResponse.setGender(false);
        jobResponse.setStartDate(LocalDateTime.of(2022, 1, 1, 0, 0, 0));
        jobResponse.setEndDate(LocalDateTime.of(2022, 1, 1, 0, 0, 0));
        jobResponse.setWorkPlace("workPlace");
        jobResponse.setCreatedAt(LocalDateTime.of(2022, 1, 1, 0, 0, 0));
        jobResponse.setUpdatedAt(LocalDateTime.of(2022, 1, 1, 0, 0, 0));
        jobResponse.setCompanyAvatar("url");
        return jobResponse;
    }

    @Test
    public void testGetListJobByWorkForm() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobRepository.getListJobByWorkForm(any(Pageable.class), eq("workForm"), eq("status")))
                .thenReturn(jobs);
        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getListJobByWorkForm(1, 10, "REMOTE");
    }

    @Test
    public void testGetListJobByWorkForm_JobRepositoryReturnsNoItems() {
        when(mockJobRepository.getListJobByWorkForm(any(Pageable.class), eq("workForm"), eq("status")))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getListJobByWorkForm(1, 10, "REMOTE");
    }

    @Test
    public void testGetListJobByWorkForm_JobHashTagServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobRepository.getListJobByWorkForm(any(Pageable.class), eq("workForm"), eq("status")))
                .thenReturn(jobs);
        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(null);

        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getListJobByWorkForm(1, 10, "REMOTE");
    }

    @Test
    public void testGetListJobByWorkForm_JobHashTagServiceReturnsNoItems() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobRepository.getListJobByWorkForm(any(Pageable.class), eq("workForm"), eq("status")))
                .thenReturn(jobs);
        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);

        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(Collections.emptyList());
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getListJobByWorkForm(1, 10, "REMOTE");
    }

    @Test
    public void testGetListJobByWorkForm_CompanyServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobRepository.getListJobByWorkForm(any(Pageable.class), eq("workForm"), eq("status")))
                .thenReturn(jobs);
        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);

        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        when(mockCompanyService.getCompanyById(1L)).thenReturn(null);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getListJobByWorkForm(1, 10, "REMOTE");
    }

    @Test
    public void testGetListJobByWorkForm_ImageServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobRepository.getListJobByWorkForm(any(Pageable.class), eq("workForm"), eq("status")))
                .thenReturn(jobs);
        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(null);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getListJobByWorkForm(1, 10, "REMOTE");
    }

//    @Test
//    public void testGetPopularJob() {
//        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
//        when(mockJobService.getPopularJobList(any(Pageable.class),null)).thenReturn(jobs);
//        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
//        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
//        final Company company = company();
//        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
//        final Optional<Image> image = Optional.ofNullable(imageEntity);
//        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
//        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getPopularJob(1, 10);
//    }

    @Test
    public void testGetPopularJob_JobServiceReturnsNoItems() {
        when(mockJobService.getPopularJobList(any(Pageable.class),any())).thenReturn(new PageImpl<>(Collections.emptyList()));
//        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, eq("hashTagName"),eq("status") ));
//        when(mockJobHashTagService.getHashTagOfJob(1)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getPopularJob(1, 10);
    }

    @Test
    public void testGetPopularJob_JobHashTagServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getPopularJobList(any(Pageable.class),any())).thenReturn(jobs);

        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(null);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getPopularJob(1, 10);
    }

    @Test
    public void testGetPopularJob_JobHashTagServiceReturnsNoItems() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getPopularJobList(any(Pageable.class),any())).thenReturn(jobs);

        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(Collections.emptyList());
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getPopularJob(1, 10);
    }

    @Test
    public void testGetPopularJob_CompanyServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getPopularJobList(any(Pageable.class),any())).thenReturn(jobs);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        when(mockCompanyService.getCompanyById(1L)).thenReturn(null);
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getPopularJob(1, 10);
    }

    @Test
    public void testGetPopularJob_ImageServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getPopularJobList(any(Pageable.class),any())).thenReturn(jobs);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(null);

        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getPopularJob(1, 10);
    }

    @Test
    public void testApproveJob() {
        final ApprovalJobRequest request = new ApprovalJobRequest();
        request.setJobId(1L);
        request.setCandidateId(1L);
        request.setApprovalStatus("approvalStatus");
        request.setCreatedAt(LocalDateTime.of(2022, 1, 1, 0, 0, 0));
        request.setUpdatedAt(LocalDateTime.of(2022, 1, 1, 0, 0, 0));
        final AppliedJob appliedJob = new AppliedJob(1L, 1L, 1L, false, "approvalStatus", false, "cvUploadUrl", false);
        when(mockAppliedJobService.getAppliedJobPendingApproval(1L, 1L)).thenReturn(appliedJob);
        final AppliedJob appliedJob1 = new AppliedJob(1L, 1L, 1L, false, "approvalStatus", false, "cvUploadUrl", false);
        when(mockAppliedJobRepository.save(any(AppliedJob.class))).thenReturn(appliedJob1);
        candidateJobServiceImplUnderTest.approveJob(request);
        verify(mockAppliedJobRepository).save(any(AppliedJob.class));
    }

    @Test
    public void testApproveJob_AppliedJobServiceReturnsNull() {
        final ApprovalJobRequest request = new ApprovalJobRequest();
        request.setJobId(1L);
        request.setCandidateId(1L);
        request.setApprovalStatus("approvalStatus");
        request.setCreatedAt(LocalDateTime.of(2022, 1, 1, 0, 0, 0));
        request.setUpdatedAt(LocalDateTime.of(2022, 1, 1, 0, 0, 0));
        when(mockAppliedJobService.getAppliedJobPendingApproval(1L, 1L)).thenReturn(null);
        assertThatThrownBy(() -> candidateJobServiceImplUnderTest.approveJob(request))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testGetJobDetail() {
        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);

        when(mockFieldsService.getById(1L)).thenReturn(new Fields(1L, "fieldName", "status"));
        final AppliedJob appliedJob = new AppliedJob(1L, 1L, 1L, false, "approvalStatus", false, "cvUploadUrl", false);
        when(mockAppliedJobService.getAppliedJobBefore(1L, 1L)).thenReturn(appliedJob);

        when(mockFollowRepository.getFollowIfHave(1L, 1L, 1L)).thenReturn(Optional.of(new Follow(1L, 1L, 1L, 1L)));
        final JobDetailResponse result = candidateJobServiceImplUnderTest.getJobDetail(1L, 1L);
    }

    @Test
    public void testGetJobDetail_CompanyServiceReturnsNull() {
        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);
        when(mockCompanyService.getCompanyById(1L)).thenReturn(null);
        assertThatThrownBy(() -> candidateJobServiceImplUnderTest.getJobDetail(1L, 1L))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testGetJobDetail_AppliedJobServiceReturnsNull() {
        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);

        when(mockFieldsService.getById(1L)).thenReturn(new Fields(1L, "fieldName", "status"));
        when(mockAppliedJobService.getAppliedJobBefore(1L, 1L)).thenReturn(null);
        when(mockFollowRepository.getFollowIfHave(1L, 1L, 1L)).thenReturn(Optional.of(new Follow(1L, 1L, 1L, 1L)));
        final JobDetailResponse result = candidateJobServiceImplUnderTest.getJobDetail(1L, 1L);
    }

    @Test
    public void testGetJobDetail_FollowRepositoryReturnsAbsent() {
        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        when(mockFieldsService.getById(1L)).thenReturn(new Fields(1L, "fieldName", "status"));
        final AppliedJob appliedJob = new AppliedJob(1L, 1L, 1L, false, "approvalStatus", false, "cvUploadUrl", false);
        when(mockAppliedJobService.getAppliedJobBefore(1L, 1L)).thenReturn(appliedJob);
        when(mockFollowRepository.getFollowIfHave(1L, 1L, 1L)).thenReturn(Optional.empty());
        final JobDetailResponse result = candidateJobServiceImplUnderTest.getJobDetail(1L, 1L);
    }

    @Test
    public void testGetPopularJob() {
        when(mockJobService.getListPopularJobId()).thenReturn(Arrays.asList(1L));
        
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(mockJobService.getPopularJobList(any(Pageable.class), eq(Arrays.asList(1L)))).thenReturn(jobs);
        
        final Optional<Payment> payment = Optional.of(new Payment());
        when(paymentService.findByJobId(1L)).thenReturn(payment);
        
        final List<JobHashtag> jobHashtags = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(jobHashtags);
        
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        
        final Optional<Image> image = Optional.of(imageEntity);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateJobServiceImplUnderTest.getPopularJob(1, 10);
        
    }
}
