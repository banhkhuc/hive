package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.CV.CVRequest;
import fpt.edu.capstone.dto.CV.CVResponse;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.job.AppliedJobRequest;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.AppliedJobRepository;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CandidateManageServiceImplTest {

    @Mock
    CVServiceImpl cvService;

    @Mock
    JobServiceImpl jobService;

    @Mock
    CandidateServiceImpl candidateService;

    @Mock
    AppliedJobServiceImpl appliedJobService;

    @Mock
    AppliedJobRepository appliedJobRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    CandidateManageServiceImpl candidateManageService;

    @Mock
    ImageServiceImpl imageService;

    @Mock
    CompanyServiceImpl companyService;
    
    @Mock
    JobHashTagServiceImpl jobHashtagService;

    @Mock
    CertificateServiceImpl certificateService;

    @Mock
    EducationServiceImpl educationService;

    @Mock
    LanguageServiceImpl languageService;

    @Mock
    MajorLevelServiceImpl majorLevelService;

    @Mock
    OtherSkillServiceImpl otherSkillService;

    @Mock
    WorkExperienceServiceImpl workExperienceService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    //JobController line 167

    @Test
    void givenRequestCvUrlIsNullAndCvServiceGetCVByCandidateIdReturnNull_whenCallAppliedJob_thenThrowException() {
        when(cvService.getCVByCandidateId(1L)).thenReturn(null);
        AppliedJobRequest request = new AppliedJobRequest();
        request.setCandidateId(1L);
        Exception exception = assertThrows(Exception.class,() -> candidateManageService.appliedJob(request));
        assertEquals("Bạn chưa tạo hồ sơ",exception.getMessage());
    }

    @Test
    void givenJobServiceExistByIdReturnFalse_whenCallAppliedJob_thenThrowException() {
        when(cvService.getCVByCandidateId(1L)).thenReturn(new CV());
        when(jobService.existsById(1L)).thenReturn(false);
        AppliedJobRequest request = new AppliedJobRequest();
        request.setCandidateId(1L);
        request.setCvUrl("abc");
        request.setJobId(1L);
        Exception exception = assertThrows(Exception.class,() -> candidateManageService.appliedJob(request));
        assertEquals(ResponseMessageConstants.JOB_DOES_NOT_EXIST,exception.getMessage());
    }

    @Test
    void givenCandidateServiceExistByIdReturnFalse_whenCallAppliedJob_thenThrowException() {
        when(cvService.getCVByCandidateId(1L)).thenReturn(new CV());
        when(jobService.existsById(1L)).thenReturn(true);
        when(candidateService.existsById(1L)).thenReturn(false);
        AppliedJobRequest request = new AppliedJobRequest();
        request.setCandidateId(1L);
        request.setCvUrl("abc");
        request.setJobId(1L);
        Exception exception = assertThrows(Exception.class,() -> candidateManageService.appliedJob(request));
        assertEquals(ResponseMessageConstants.USER_DOES_NOT_EXIST,exception.getMessage());
    }

    @Test
    void givenAppliedJobServiceGetAppliedJobBeforeReturnNullAndModelMapperMapReturnNull_whenCallAppliedJob_thenAppliedRepositoryAreNotCalled() {
        AppliedJobRequest request = new AppliedJobRequest();
        request.setCandidateId(1L);
        request.setCvUrl("abc");
        request.setJobId(1L);

        when(cvService.getCVByCandidateId(1L)).thenReturn(new CV());
        when(jobService.existsById(1L)).thenReturn(true);
        when(candidateService.existsById(1L)).thenReturn(true);
        when(appliedJobService.getAppliedJobBefore(1L,1L)).thenReturn(null);

        Exception exception = assertThrows(Exception.class,() -> candidateManageService.appliedJob(request));
        verify(appliedJobRepository,times(0)).save(any());
    }

    @Test
    void givenAppliedJobExistAndStatusIsPending_whenCallAppliedJob_thenSaveToDB() throws Exception {
        when(cvService.getCVByCandidateId(1L)).thenReturn(new CV());
        when(jobService.existsById(1L)).thenReturn(true);
        when(candidateService.existsById(1L)).thenReturn(true);
        AppliedJob appliedJob = new AppliedJob();
        appliedJob.setApplied(true);
        appliedJob.setApprovalStatus(Enums.ApprovalStatus.PENDING.getStatus());
        when(appliedJobService.getAppliedJobBefore(1L,1L)).thenReturn(appliedJob);
        AppliedJobRequest request = new AppliedJobRequest();
        request.setCandidateId(1L);
        request.setCvUrl("abc");
        request.setJobId(1L);

        candidateManageService.appliedJob(request);

        verify(appliedJobRepository,times(1)).save(any());
    }

    @Test
    void givenAppliedJobExistAndStatusIsApproved_whenCallAppliedJob_thenThrowException() {
        when(cvService.getCVByCandidateId(1L)).thenReturn(new CV());
        when(jobService.existsById(1L)).thenReturn(true);
        when(candidateService.existsById(1L)).thenReturn(true);
        AppliedJob appliedJob = new AppliedJob();
        appliedJob.setApplied(true);
        appliedJob.setApprovalStatus(Enums.ApprovalStatus.APPROVED.getStatus());
        when(appliedJobService.getAppliedJobBefore(1L,1L)).thenReturn(appliedJob);
        AppliedJobRequest request = new AppliedJobRequest();
        request.setCandidateId(1L);
        request.setCvUrl("abc");
        request.setJobId(1L);

        Exception exception = assertThrows(Exception.class,() -> candidateManageService.appliedJob(request));

        assertEquals("CV này đã được chấp nhận.",exception.getMessage());
    }

    @Test
    void givenAppliedJobExistAndStatusIsReject_whenCallAppliedJob_thenSaveToDB() throws Exception {
        AppliedJobRequest request = new AppliedJobRequest();
        request.setCandidateId(1L);
        request.setCvUrl("abc");
        request.setJobId(1L);

        when(cvService.getCVByCandidateId(1L)).thenReturn(new CV());
        when(jobService.existsById(1L)).thenReturn(true);
        when(candidateService.existsById(1L)).thenReturn(true);
        Object AppliedJobRequest = request;
        when(modelMapper.map(AppliedJobRequest,AppliedJob.class)).thenReturn(new AppliedJob());
        AppliedJob appliedJob = new AppliedJob();
        appliedJob.setApplied(true);
        appliedJob.setApprovalStatus(Enums.ApprovalStatus.REJECT.getStatus());
        when(appliedJobService.getAppliedJobBefore(1L,1L)).thenReturn(appliedJob);

        candidateManageService.appliedJob(request);

        verify(appliedJobRepository,times(1)).save(any());
    }

    @Test
    void givenAppliedJobIsAppliedReturnFalse_whenCallAppliedJob_thenSaveToDB() throws Exception {
        when(cvService.getCVByCandidateId(1L)).thenReturn(new CV());
        when(jobService.existsById(1L)).thenReturn(true);
        when(candidateService.existsById(1L)).thenReturn(true);
        AppliedJob appliedJob = new AppliedJob();
        appliedJob.setApplied(false);
        appliedJob.setApprovalStatus(Enums.ApprovalStatus.APPROVED.getStatus());
        when(appliedJobService.getAppliedJobBefore(1L,1L)).thenReturn(appliedJob);
        AppliedJobRequest request = new AppliedJobRequest();
        request.setCandidateId(1L);
        request.setCvUrl("abc");
        request.setJobId(1L);

        candidateManageService.appliedJob(request);

        verify(appliedJobRepository,times(1)).save(any());
    }

    @Test
    void givenAppliedJobIsNull_whenCallAppliedJob_thenSaveToDB() throws Exception {
        AppliedJobRequest request = new AppliedJobRequest();
        request.setCandidateId(1L);
        request.setCvUrl("abc");
        request.setJobId(1L);
        when(cvService.getCVByCandidateId(1L)).thenReturn(new CV());
        when(jobService.existsById(1L)).thenReturn(true);
        when(candidateService.existsById(1L)).thenReturn(true);
        AppliedJob appliedJob = new AppliedJob();
        appliedJob.setApplied(false);
        appliedJob.setApprovalStatus(Enums.ApprovalStatus.APPROVED.getStatus());
        when(appliedJobService.getAppliedJobBefore(1L,1L)).thenReturn(null);
        Object AppliedJobRequest = request;
        when(modelMapper.map(AppliedJobRequest,AppliedJob.class)).thenReturn(new AppliedJob());

        candidateManageService.appliedJob(request);

        verify(appliedJobRepository,times(1)).save(any());
    }
    
    @Test
    public void testSearchAppliedJobsOfCandidate_AppliedJobServiceReturnsNoItems() {
        // Setup
        when(candidateService.existsById(1L)).thenReturn(false);
        when(appliedJobService.searchAppliedJobsOfCandidate(any(Pageable.class), eq(1L),
                eq("approvalStatus"))).thenReturn(new PageImpl<>(Collections.emptyList()));
        final Job job = job();
        when(jobService.getJobById(1L)).thenReturn(job);
        final Optional<Image> image = Optional.of(
                new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
        when(imageService.getImageCompany(1L, true)).thenReturn(image);
        final Company company = new Company();
        company.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setId(1L);
        company.setFieldWork("fieldWork");
        company.setName("companyName");
        company.setEmail("email");
        company.setPhone("phone");
        company.setDescription("description");
        company.setWebsite("website");
        company.setNumberEmployees("numberEmployees");
        company.setAddress("address");
        company.setTaxCode("taxCode");
        company.setIsDeleted(0);
        company.setMapUrl("mapUrl");
        company.setCreatorId(1L);
        when(companyService.getCompanyById(1L)).thenReturn(company);
        final ResponseDataPagination result = candidateManageService.searchAppliedJobsOfCandidate(1, 10, 1L,
                "approvalStatus");
    }

    @Test
    public void testSearchAppliedJobsOfCandidate_ImageServiceReturnsAbsent() {
        when(candidateService.existsById(1L)).thenReturn(false);
        final Page<AppliedJob> appliedJobs = new PageImpl<>(
                Arrays.asList(new AppliedJob(1L, 1L, 1L, false, "approvalStatus", false, "cvUploadUrl", false)));
        when(appliedJobService.searchAppliedJobsOfCandidate(any(Pageable.class), eq(1L),
                eq("approvalStatus"))).thenReturn(appliedJobs);

        // Configure JobService.getJobById(...).
        final Job job = job();
        when(jobService.getJobById(1L)).thenReturn(job);
        when(imageService.getImageCompany(1L, true)).thenReturn(Optional.empty());
        final Company company = new Company();
        company.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setId(1L);
        company.setFieldWork("fieldWork");
        company.setName("companyName");
        company.setEmail("email");
        company.setPhone("phone");
        company.setDescription("description");
        company.setWebsite("website");
        company.setNumberEmployees("numberEmployees");
        company.setAddress("address");
        company.setTaxCode("taxCode");
        company.setIsDeleted(0);
        company.setMapUrl("mapUrl");
        company.setCreatorId(1L);
        when(companyService.getCompanyById(1L)).thenReturn(company);
        final ResponseDataPagination result = candidateManageService.searchAppliedJobsOfCandidate(1, 10, 1L,
                "approvalStatus");
    }

    @Test
    public void testGetJobsOfCompany() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(jobService.getJobByCompanyId(0, 0, 1L)).thenReturn(jobs);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(jobHashtagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = new Company();
        company.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setId(1L);
        company.setFieldWork("fieldWork");
        company.setName("companyName");
        company.setEmail("email");
        company.setPhone("phone");
        company.setDescription("description");
        company.setWebsite("website");
        company.setNumberEmployees("numberEmployees");
        company.setAddress("address");
        company.setTaxCode("taxCode");
        company.setIsDeleted(0);
        company.setMapUrl("mapUrl");
        company.setCreatorId(1L);
        when(companyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.of(
                new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
        when(imageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateManageService.getJobsOfCompany(0, 0, 1L);
    }

    @Test
    public void testGetJobsOfCompany_JobServiceReturnsNoItems() {
        when(jobService.getJobByCompanyId(0, 0, 1L)).thenReturn(new PageImpl<>(Collections.emptyList()));
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(jobHashtagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = new Company();
        company.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setId(1L);
        company.setFieldWork("fieldWork");
        company.setName("companyName");
        company.setEmail("email");
        company.setPhone("phone");
        company.setDescription("description");
        company.setWebsite("website");
        company.setNumberEmployees("numberEmployees");
        company.setAddress("address");
        company.setTaxCode("taxCode");
        company.setIsDeleted(0);
        company.setMapUrl("mapUrl");
        company.setCreatorId(1L);
        when(companyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.of(
                new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
        when(imageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateManageService.getJobsOfCompany(0, 0, 1L);
    }

    @Test
    public void testGetJobsOfCompany_jobHashtagServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(jobService.getJobByCompanyId(0, 0, 1L)).thenReturn(jobs);
        when(jobHashtagService.getHashTagOfJob(1L)).thenReturn(null);
        final Company company = new Company();
        company.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setId(1L);
        company.setFieldWork("fieldWork");
        company.setName("companyName");
        company.setEmail("email");
        company.setPhone("phone");
        company.setDescription("description");
        company.setWebsite("website");
        company.setNumberEmployees("numberEmployees");
        company.setAddress("address");
        company.setTaxCode("taxCode");
        company.setIsDeleted(0);
        company.setMapUrl("mapUrl");
        company.setCreatorId(1L);
        when(companyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.of(
                new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
        when(imageService.getImageCompany(1L, true)).thenReturn(image);
//        final ResponseDataPagination result = candidateManageService.getJobsOfCompany(1, 10, 1L);
    }

    @Test
    public void testGetJobsOfCompany_jobHashtagServiceReturnsNoItems() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(jobService.getJobByCompanyId(0, 0, 1L)).thenReturn(jobs);
        when(jobHashtagService.getHashTagOfJob(1L)).thenReturn(Collections.emptyList());
        final Company company = new Company();
        company.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setId(1L);
        company.setFieldWork("fieldWork");
        company.setName("companyName");
        company.setEmail("email");
        company.setPhone("phone");
        company.setDescription("description");
        company.setWebsite("website");
        company.setNumberEmployees("numberEmployees");
        company.setAddress("address");
        company.setTaxCode("taxCode");
        company.setIsDeleted(0);
        company.setMapUrl("mapUrl");
        company.setCreatorId(1L);
        when(companyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.of(
                new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
        when(imageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateManageService.getJobsOfCompany(0, 0, 1L);
    }
    Job job(){
        Job job =  new Job(1L, 1L, "jobName", "workPlace", "workForm", LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0), 1L, 1L, 1L, "rank", "experience", false,
                "jobDescription", "jobRequirement", "benefit", 1L, 0, 1L, "weekday", 1L,
                "flag", "academicLevel");
        return job;
    }

    @Test
    public void testGetJobsOfCompany_CompanyServiceReturnsNull() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(jobService.getJobByCompanyId(0, 0, 1L)).thenReturn(jobs);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(jobHashtagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        when(companyService.getCompanyById(1L)).thenReturn(null);
        final Optional<Image> image = Optional.of(
                new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
        when(imageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateManageService.getJobsOfCompany(0, 0, 1L);
    }

    @Test
    public void testGetJobsOfCompany_ImageServiceReturnsAbsent() {
        final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
        when(jobService.getJobByCompanyId(0, 0, 1L)).thenReturn(jobs);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(jobHashtagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = new Company();
        when(companyService.getCompanyById(1L)).thenReturn(company);
        when(imageService.getImageCompany(1L, true)).thenReturn(Optional.empty());
//        final ResponseDataPagination result = candidateManageService.getJobsOfCompany(1, 10, 1L);
    }

    @Test
    public void testFindCvByCandidateId() {
        final CVResponse expectedResult = new CVResponse(1L, 1L, 1L, "summary", LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Arrays.asList(certificate()), Arrays.asList(education()),
                Arrays.asList(language()),
                Arrays.asList(majorLevel()),
                Arrays.asList(otherSkill()), Arrays.asList(workExperience()));
        final List<CV> cvList = Arrays.asList(cv());
        when(cvService.findCvByCandidateId(1L)).thenReturn(cvList);
        final List<Certificate> certificates = Arrays.asList(
                certificate());
        when(certificateService.getListCertificateByCvId(1L)).thenReturn(certificates);
        final List<Education> educationList = Arrays.asList(education());
        when(educationService.getListEducationByCvId(1L)).thenReturn(educationList);
        final List<Language> list = Arrays.asList(language());
        when(languageService.getListLanguageByCvId(1L)).thenReturn(list);
        final List<MajorLevel> majorLevels = Arrays.asList(majorLevel());
        when(majorLevelService.getListMajorLevelByCvId(1L)).thenReturn(majorLevels);
        final List<OtherSkill> otherSkills = Arrays.asList(otherSkill());
        when(otherSkillService.getListOtherSkillByCvId(1L)).thenReturn(otherSkills);
        final List<WorkExperience> workExperiences = Arrays.asList(workExperience());
        when(workExperienceService.getListWorkExperienceByCvId(1L)).thenReturn(workExperiences);
        final CVResponse result = candidateManageService.findCvByCandidateId(1L);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void testFindCvByCandidateId_CVServiceReturnsNull() {
        when(cvService.findCvByCandidateId(1L)).thenReturn(null);
        assertThatThrownBy(() -> candidateManageService.findCvByCandidateId(1L))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testFindCvByCandidateId_CVServiceReturnsNoItems() {
        when(cvService.findCvByCandidateId(1L)).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> candidateManageService.findCvByCandidateId(1L))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testFindCvByCandidateId_CertificateServiceReturnsNoItems() {
        final CVResponse expectedResult = new CVResponse(1L, 1L, 1L, "summary", LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Arrays.asList(certificate()), Arrays.asList(education()),
                Arrays.asList(language()),
                Arrays.asList(majorLevel()),
                Arrays.asList(otherSkill()), Arrays.asList(workExperience()));
        final List<CV> cvList = Arrays.asList(cv());
        when(cvService.findCvByCandidateId(1L)).thenReturn(cvList);
        when(certificateService.getListCertificateByCvId(1L)).thenReturn(Collections.emptyList());
        final List<Education> educationList = Arrays.asList(education());
        when(educationService.getListEducationByCvId(1L)).thenReturn(educationList);
        final List<Language> list = Arrays.asList(language());
        when(languageService.getListLanguageByCvId(1L)).thenReturn(list);
        final List<MajorLevel> majorLevels = Arrays.asList(majorLevel());
        when(majorLevelService.getListMajorLevelByCvId(1L)).thenReturn(majorLevels);
        final List<OtherSkill> otherSkills = Arrays.asList(otherSkill());
        when(otherSkillService.getListOtherSkillByCvId(1L)).thenReturn(otherSkills);
        final List<WorkExperience> workExperiences = Arrays.asList(workExperience());
        when(workExperienceService.getListWorkExperienceByCvId(1L)).thenReturn(workExperiences);
        final CVResponse result = candidateManageService.findCvByCandidateId(1L);
        assertThat(result).isEqualTo(expectedResult);
    }

    Language language(){
        Language language = new Language(1L, "language", "level", 1L);
        return language;
    }
    
    Certificate certificate(){
        Certificate certificate = new Certificate(1L, "certificateName", "certificateUrl", 1L, 1L);
        return certificate;
    }
    
    Education education(){
        Education education = new Education(1L, 1L, "school", "major", LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0), false, "description");
        return education;
    }
    
    MajorLevel majorLevel(){
        MajorLevel majorLevel = new MajorLevel(1L, 1L, 1L, 1L, "level", false);
        return majorLevel;
    }
    
    OtherSkill otherSkill(){
        OtherSkill otherSkill = new OtherSkill(1L, "skillName", 1L, "level");
        return otherSkill;
    }
    
    WorkExperience workExperience(){
        WorkExperience workExperience =  new WorkExperience(1L, 1L, "companyName", "position", LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0), "description", false);
        return workExperience;
    }
    
    CV cv(){
        CV cv = new CV(1L, 1L, 1L, "summary", "totalExperienceYear");
        return cv;
    }
    @Test
    public void testFindCvByCandidateId_EducationServiceReturnsNoItems() {
        final CVResponse expectedResult = new CVResponse(1L, 1L, 1L, "summary", LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Arrays.asList(certificate()), Arrays.asList(education()),
                Arrays.asList(language()),
                Arrays.asList(majorLevel()),
                Arrays.asList(otherSkill()), Arrays.asList(workExperience()));
        final List<CV> cvList = Arrays.asList(cv());
        when(cvService.findCvByCandidateId(1L)).thenReturn(cvList);
        final List<Certificate> certificates = Arrays.asList(
                certificate());
        when(certificateService.getListCertificateByCvId(1L)).thenReturn(certificates);
        when(educationService.getListEducationByCvId(1L)).thenReturn(Collections.emptyList());
        final List<Language> list = Arrays.asList(language());
        when(languageService.getListLanguageByCvId(1L)).thenReturn(list);
        final List<MajorLevel> majorLevels = Arrays.asList(majorLevel());
        when(majorLevelService.getListMajorLevelByCvId(1L)).thenReturn(majorLevels);
        final List<OtherSkill> otherSkills = Arrays.asList(otherSkill());
        when(otherSkillService.getListOtherSkillByCvId(1L)).thenReturn(otherSkills);
        final List<WorkExperience> workExperiences = Arrays.asList(workExperience());
        when(workExperienceService.getListWorkExperienceByCvId(1L)).thenReturn(workExperiences);
        final CVResponse result = candidateManageService.findCvByCandidateId(1L);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void testFindCvByCandidateId_LanguageServiceReturnsNoItems() {
        final CVResponse expectedResult = new CVResponse(1L, 1L, 1L, "summary", LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Arrays.asList(certificate()), Arrays.asList(education()),
                Arrays.asList(language()),
                Arrays.asList(majorLevel()),
                Arrays.asList(otherSkill()), Arrays.asList(workExperience()));
        final List<CV> cvList = Arrays.asList(cv());
        when(cvService.findCvByCandidateId(1L)).thenReturn(cvList);
        final List<Certificate> certificates = Arrays.asList(
                certificate());
        when(certificateService.getListCertificateByCvId(1L)).thenReturn(certificates);
        final List<Education> educationList = Arrays.asList(education());
        when(educationService.getListEducationByCvId(1L)).thenReturn(educationList);
        when(languageService.getListLanguageByCvId(1L)).thenReturn(Collections.emptyList());
        final List<MajorLevel> majorLevels = Arrays.asList(majorLevel());
        when(majorLevelService.getListMajorLevelByCvId(1L)).thenReturn(majorLevels);
        final List<OtherSkill> otherSkills = Arrays.asList(otherSkill());
        when(otherSkillService.getListOtherSkillByCvId(1L)).thenReturn(otherSkills);
        final List<WorkExperience> workExperiences = Arrays.asList(workExperience());
        when(workExperienceService.getListWorkExperienceByCvId(1L)).thenReturn(workExperiences);
        final CVResponse result = candidateManageService.findCvByCandidateId(1L);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void testFindCvByCandidateId_MajorLevelServiceReturnsNoItems() {
        final CVResponse expectedResult = new CVResponse(1L, 1L, 1L, "summary", LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Arrays.asList(certificate()), Arrays.asList(education()),
                Arrays.asList(language()),
                Arrays.asList(majorLevel()),
                Arrays.asList(otherSkill()), Arrays.asList(workExperience()));
        final List<CV> cvList = Arrays.asList(cv());
        when(cvService.findCvByCandidateId(1L)).thenReturn(cvList);
        final List<Certificate> certificates = Arrays.asList(
                certificate());
        when(certificateService.getListCertificateByCvId(1L)).thenReturn(certificates);
        final List<Education> educationList = Arrays.asList(education());
        when(educationService.getListEducationByCvId(1L)).thenReturn(educationList);
        final List<Language> list = Arrays.asList(language());
        when(languageService.getListLanguageByCvId(1L)).thenReturn(list);

        when(majorLevelService.getListMajorLevelByCvId(1L)).thenReturn(Collections.emptyList());
        final List<OtherSkill> otherSkills = Arrays.asList(otherSkill());
        when(otherSkillService.getListOtherSkillByCvId(1L)).thenReturn(otherSkills);
        final List<WorkExperience> workExperiences = Arrays.asList(workExperience());
        when(workExperienceService.getListWorkExperienceByCvId(1L)).thenReturn(workExperiences);
        final CVResponse result = candidateManageService.findCvByCandidateId(1L);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void testFindCvByCandidateId_OtherSkillServiceReturnsNoItems() {
        final CVResponse expectedResult = new CVResponse(1L, 1L, 1L, "summary", LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Arrays.asList(certificate()), Arrays.asList(education()),
                Arrays.asList(language()),
                Arrays.asList(majorLevel()),
                Arrays.asList(otherSkill()), Arrays.asList(workExperience()));
        final List<CV> cvList = Arrays.asList(cv());
        when(cvService.findCvByCandidateId(1L)).thenReturn(cvList);
        final List<Certificate> certificates = Arrays.asList(
                certificate());
        when(certificateService.getListCertificateByCvId(1L)).thenReturn(certificates);
        final List<Education> educationList = Arrays.asList(education());
        when(educationService.getListEducationByCvId(1L)).thenReturn(educationList);
        final List<Language> list = Arrays.asList(language());
        when(languageService.getListLanguageByCvId(1L)).thenReturn(list);
        final List<MajorLevel> majorLevels = Arrays.asList(majorLevel());
        when(majorLevelService.getListMajorLevelByCvId(1L)).thenReturn(majorLevels);
        when(otherSkillService.getListOtherSkillByCvId(1L)).thenReturn(Collections.emptyList());
        final List<WorkExperience> workExperiences = Arrays.asList(workExperience());
        when(workExperienceService.getListWorkExperienceByCvId(1L)).thenReturn(workExperiences);
        final CVResponse result = candidateManageService.findCvByCandidateId(1L);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void testFindCvByCandidateId_WorkExperienceServiceReturnsNoItems() {
        final CVResponse expectedResult = new CVResponse(1L, 1L, 1L, "summary", LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Arrays.asList(certificate()), Arrays.asList(education()),
                Arrays.asList(language()),
                Arrays.asList(majorLevel()),
                Arrays.asList(otherSkill()), Arrays.asList(workExperience()));
        final List<CV> cvList = Arrays.asList(cv());
        when(cvService.findCvByCandidateId(1L)).thenReturn(cvList);
        final List<Certificate> certificates = Arrays.asList(
                certificate());
        when(certificateService.getListCertificateByCvId(1L)).thenReturn(certificates);
        final List<Education> educationList = Arrays.asList(education());
        when(educationService.getListEducationByCvId(1L)).thenReturn(educationList);
        final List<Language> list = Arrays.asList(language());
        when(languageService.getListLanguageByCvId(1L)).thenReturn(list);
        final List<MajorLevel> majorLevels = Arrays.asList(majorLevel());
        when(majorLevelService.getListMajorLevelByCvId(1L)).thenReturn(majorLevels);
        final List<OtherSkill> otherSkills = Arrays.asList(otherSkill());
        when(otherSkillService.getListOtherSkillByCvId(1L)).thenReturn(otherSkills);
        when(workExperienceService.getListWorkExperienceByCvId(1L)).thenReturn(Collections.emptyList());
        final CVResponse result = candidateManageService.findCvByCandidateId(1L);
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void testCreateCV() {
        final CVRequest request = new CVRequest(1L, "summary", "totalExperienceYear");
        final Candidate candidate = candidate();
        when(candidateService.getCandidateById(1L)).thenReturn(candidate);
        final List<CV> cvList = Arrays.asList(cv());
        when(cvService.findCvByCandidateId(1L)).thenReturn(cvList);
        final CV cv = cv();
        when(modelMapper.map(any(Object.class), eq(CV.class))).thenReturn(cv);
        final CV result = candidateManageService.createCV(request);
        verify(cvService).save(any(CV.class));
    }

    @Test
    public void testCreateCV_CandidateServiceReturnsNull() {
        final CVRequest request = new CVRequest(1L, "summary", "totalExperienceYear");
        when(candidateService.getCandidateById(1L)).thenReturn(null);
        assertThatThrownBy(() -> candidateManageService.createCV(request))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testCreateCV_CVServiceFindCvByCandidateIdReturnsNoItems() {
        final CVRequest request = new CVRequest(1L, "summary", "totalExperienceYear");
        final Candidate candidate = candidate();
        when(candidateService.getCandidateById(1L)).thenReturn(candidate);
        when(cvService.findCvByCandidateId(1L)).thenReturn(Collections.emptyList());
        final CV cv = cv();
        when(modelMapper.map(any(Object.class), eq(CV.class))).thenReturn(cv);
        final CV result = candidateManageService.createCV(request);
        verify(cvService).save(any(CV.class));
    }

    @Mock
    RecruiterServiceImpl recruiterService;
    
    @Mock
    ProfileViewerServiceImpl profileViewerService;
    
    Recruiter recruiter(){
        Recruiter recruiter =new Recruiter(1L, 1L, "companyName", "fullName", false, false, "position",
                "linkedinAccount", "additionalLicense", "businessLicenseUrl", "additionalLicenseUrl", 1L, false,
                "companyAddress", "businessLicenseApprovalStatus", "additionalLicenseApprovalStatus", "avatarUrl", 0);
        return recruiter;
    }
    @Test
    public void testGetProfileViewer() {
        final Candidate candidate = candidate();
        when(candidateService.getCandidateById(1L)).thenReturn(candidate);
        final Optional<CV> cv = Optional.of(cv());
        when(cvService.findByIdAndCandidateId(1L, 1L)).thenReturn(cv);
        final ProfileViewer viewer = new ProfileViewer();
        viewer.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        viewer.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        viewer.setId(1L);
        viewer.setCvId(1L);
        viewer.setViewerId(1L);
        viewer.setCandidateId(1L);
        final Page<ProfileViewer> viewerPage = new PageImpl<>(Arrays.asList(viewer));
        when(profileViewerService.getProfileViewerOfCv(any(Pageable.class), eq(1L))).thenReturn(viewerPage);
        final Recruiter recruiter = recruiter();
        when(recruiterService.getRecruiterById(1L)).thenReturn(recruiter);
        final Company company = new Company();
        company.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setId(1L);
        company.setFieldWork("fieldWork");
        company.setName("companyName");
        company.setEmail("email");
        company.setPhone("phone");
        company.setDescription("description");
        company.setWebsite("website");
        company.setNumberEmployees("numberEmployees");
        company.setAddress("address");
        company.setTaxCode("taxCode");
        company.setIsDeleted(0);
        company.setMapUrl("mapUrl");
        company.setCreatorId(1L);
        when(companyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.of(
                new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
        when(imageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateManageService.getProfileViewer(1, 10, 1L, 1L);
    }

    @Test
    public void testGetProfileViewer_CandidateServiceReturnsNull() {
        when(candidateService.getCandidateById(1L)).thenReturn(null);
        assertThatThrownBy(() -> candidateManageService.getProfileViewer(0, 0, 1L, 1L))
                .isInstanceOf(HiveConnectException.class);
    }

    Candidate candidate(){
        Candidate candidate = new Candidate(1L, 1L, false, LocalDateTime.of(2020, 1, 1, 0, 0, 0), "searchHistory",
                "country", "fullName", "address", "socialLink", "avatarUrl", false, 1L, "introduction");
        return candidate;
    }
    @Test
    public void testGetProfileViewer_CVServiceReturnsAbsent() {
        final Candidate candidate = candidate();
        when(candidateService.getCandidateById(1L)).thenReturn(candidate);
        when(cvService.findByIdAndCandidateId(1L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> candidateManageService.getProfileViewer(0, 0, 1L, 1L))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testGetProfileViewer_ProfileViewerServiceReturnsNoItems() {
        final Candidate candidate = candidate();
        when(candidateService.getCandidateById(1L)).thenReturn(candidate);
        final Optional<CV> cv = Optional.of(cv());
        when(cvService.findByIdAndCandidateId(1L, 1L)).thenReturn(cv);

        when(profileViewerService.getProfileViewerOfCv(any(Pageable.class), eq(1L)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final Recruiter recruiter = recruiter();
        when(recruiterService.getRecruiterById(1L)).thenReturn(recruiter);
        final Company company = new Company();
        company.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setId(1L);
        company.setFieldWork("fieldWork");
        company.setName("companyName");
        company.setEmail("email");
        company.setPhone("phone");
        company.setDescription("description");
        company.setWebsite("website");
        company.setNumberEmployees("numberEmployees");
        company.setAddress("address");
        company.setTaxCode("taxCode");
        company.setIsDeleted(0);
        company.setMapUrl("mapUrl");
        company.setCreatorId(1L);
        when(companyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.of(
                new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
        when(imageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateManageService.getProfileViewer(1, 10, 1L, 1L);
    }

    @Test
    public void testGetProfileViewer_RecruiterServiceReturnsNull() {
        final Candidate candidate = candidate();
        when(candidateService.getCandidateById(1L)).thenReturn(candidate);
        final Optional<CV> cv = Optional.of(cv());
        when(cvService.findByIdAndCandidateId(1L, 1L)).thenReturn(cv);
        final ProfileViewer viewer = new ProfileViewer();
        viewer.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        viewer.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        viewer.setId(1L);
        viewer.setCvId(1L);
        viewer.setViewerId(1L);
        viewer.setCandidateId(1L);
        final Page<ProfileViewer> viewerPage = new PageImpl<>(Arrays.asList(viewer));
        when(profileViewerService.getProfileViewerOfCv(any(Pageable.class), eq(1L))).thenReturn(viewerPage);
        when(recruiterService.getRecruiterById(1L)).thenReturn(null);
        final Company company = new Company();
        company.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setId(1L);
        company.setFieldWork("fieldWork");
        company.setName("companyName");
        company.setEmail("email");
        company.setPhone("phone");
        company.setDescription("description");
        company.setWebsite("website");
        company.setNumberEmployees("numberEmployees");
        company.setAddress("address");
        company.setTaxCode("taxCode");
        company.setIsDeleted(0);
        company.setMapUrl("mapUrl");
        company.setCreatorId(1L);
        when(companyService.getCompanyById(1L)).thenReturn(company);
        final Optional<Image> image = Optional.of(
                new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
        when(imageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = candidateManageService.getProfileViewer(1, 10, 1L, 1L);
    }

    @Test
    public void testGetProfileViewer_CompanyServiceReturnsNull() {
        final Candidate candidate = candidate();
        when(candidateService.getCandidateById(1L)).thenReturn(candidate);
        final Optional<CV> cv = Optional.of(cv());
        when(cvService.findByIdAndCandidateId(1L, 1L)).thenReturn(cv);
        final ProfileViewer viewer = new ProfileViewer();
        viewer.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        viewer.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        viewer.setId(1L);
        viewer.setCvId(1L);
        viewer.setViewerId(1L);
        viewer.setCandidateId(1L);
        final Page<ProfileViewer> viewerPage = new PageImpl<>(Arrays.asList(viewer));
        when(profileViewerService.getProfileViewerOfCv(any(Pageable.class), eq(1L))).thenReturn(viewerPage);
        final Recruiter recruiter = recruiter();
        when(recruiterService.getRecruiterById(1L)).thenReturn(recruiter);

        when(companyService.getCompanyById(1L)).thenReturn(null);
        final Optional<Image> image = Optional.of(
                new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
        when(imageService.getImageCompany(1L, true)).thenReturn(image);
//        final ResponseDataPagination result = candidateManageService.getProfileViewer(1, 10, 1L, 1L);
    }

    @Test
    public void testGetProfileViewer_ImageServiceReturnsNull() {
        final Candidate candidate = candidate();
        when(candidateService.getCandidateById(1L)).thenReturn(candidate);
        final Optional<CV> cv = Optional.of(cv());
        when(cvService.findByIdAndCandidateId(1L, 1L)).thenReturn(cv);
        final ProfileViewer viewer = new ProfileViewer();
        viewer.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        viewer.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        viewer.setId(1L);
        viewer.setCvId(1L);
        viewer.setViewerId(1L);
        viewer.setCandidateId(1L);
        final Page<ProfileViewer> viewerPage = new PageImpl<>(Arrays.asList(viewer));
        when(profileViewerService.getProfileViewerOfCv(any(Pageable.class), eq(1L))).thenReturn(viewerPage);
        final Recruiter recruiter = recruiter();
        when(recruiterService.getRecruiterById(1L)).thenReturn(recruiter);
        final Company company = new Company();
        company.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setUpdatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        company.setId(1L);
        company.setFieldWork("fieldWork");
        company.setName("companyName");
        company.setEmail("email");
        company.setPhone("phone");
        company.setDescription("description");
        company.setWebsite("website");
        company.setNumberEmployees("numberEmployees");
        company.setAddress("address");
        company.setTaxCode("taxCode");
        company.setIsDeleted(0);
        company.setMapUrl("mapUrl");
        company.setCreatorId(1L);
        when(companyService.getCompanyById(1L)).thenReturn(company);
        when(imageService.getImageCompany(1L, true)).thenReturn(Optional.empty());
//        final ResponseDataPagination result = candidateManageService.getProfileViewer(1, 10, 1L, 1L);
    }
}