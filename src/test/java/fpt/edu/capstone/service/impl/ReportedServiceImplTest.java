package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.admin.user.ReportedUserResponse;
import fpt.edu.capstone.dto.job.ReportJobRequest;
import fpt.edu.capstone.dto.job.ReportedJobResponse;
import fpt.edu.capstone.entity.Job;
import fpt.edu.capstone.entity.Recruiter;
import fpt.edu.capstone.entity.Report;
import fpt.edu.capstone.entity.Users;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.ReportedRepository;
import fpt.edu.capstone.service.JobService;
import fpt.edu.capstone.service.RecruiterService;
import fpt.edu.capstone.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportedServiceImplTest {

    @Mock
    private ReportedRepository mockReportedRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private JobService mockJobService;
    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private RecruiterService mockRecruiterService;

    private ReportedServiceImpl reportedServiceImplUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        reportedServiceImplUnderTest = new ReportedServiceImpl(mockReportedRepository, mockUserService, mockJobService,
                mockModelMapper, mockRecruiterService);
    }

    private Users users(){
        Users users = new Users();
        users.setId(1L);
        users.setUsername("username");
        users.setPassword("password");
        users.setEmail("email");
        users.setPhone("0967445450");
        users.setRoleId(1L);
        users.setIsDeleted(0);
        users.setLastLoginTime(LocalDateTime.now());
        users.setVerifiedEmail(false);
        users.setVerifiedPhone(false);
        users.setActive(true);
        users.setLocked(false);
        users.setAvatar("avatar");
        users.setResetPasswordToken("setResetPasswordToken");
        users.setGoogle(false);
        return users;
    }

    private ReportJobRequest request(){
        ReportJobRequest request = new ReportJobRequest();
        request.setJobId(1L);
        request.setReportReason("reportReason");
        request.setRelatedLink("relatedLink");
        request.setFullName("fullName");
        request.setPhone("phone");
        request.setUserAddress("userAddress");
        request.setUserEmail("userEmail");
        return request;
    }

    private Job job(){
        Job job = new Job();
        job.setId(1L);
        job.setCompanyId(1L);
        job.setJobName("jobName");
        job.setWorkPlace("workPlace");
        job.setWorkForm("workForm");
        job.setStartDate(LocalDateTime.now());
        job.setEndDate(LocalDateTime.now().plusDays(10));
        job.setFromSalary(1L);
        job.setToSalary(1L);
        job.setNumberRecruits(1L);
        job.setRank("rank");
        job.setExperience("experience");
        job.setGender(false);
        job.setJobDescription("jobDescription");
        job.setJobRequirement("jobRequirement");
        job.setBenefit("benefit");
        job.setFieldId(1L);
        job.setIsDeleted(0);
        job.setRecruiterId(1L);
        job.setWeekday("weekday");
        job.setCountryId(1L);
        job.setFlag("Posted");
        return job;
    }

    private Report report(){
        Report report = new Report();
        report.setId(1L);
        report.setReportReason("reportReason");
        report.setPersonReportId(1L);
        report.setRelatedLink("relatedLink");
        report.setApprovalReportedStatus("approvalReportedStatus");
        report.setFullName("fullName");
        report.setPhone("0967445450");
        report.setUserAddress("userAddress");
        report.setUserEmail("userEmail");
        report.setReportType("reportType");
        report.setJobId(1L);
        return report;
    }

    private Recruiter recruiter(){
        Recruiter recruiter = new Recruiter();
        recruiter.setId(1L);
        recruiter.setCompanyId(1L);
        recruiter.setCompanyName("companyName");
        recruiter.setFullName("fullName");
        recruiter.setVerifyAccount(true);
        recruiter.setGender(false);
        recruiter.setPosition("HR");
        recruiter.setLinkedinAccount("linkedinAccount");
        recruiter.setAdditionalLicense("additionalLicense");
        recruiter.setBusinessLicenseUrl("businessLicenseUrl");
        recruiter.setAdditionalLicenseUrl("additionalLicenseUrl");
        recruiter.setUserId(1L);
        recruiter.setDeleted(false);
        recruiter.setCompanyAddress("companyAddress");
        recruiter.setBusinessLicenseApprovalStatus("businessLicenseApprovalStatus");
        recruiter.setAvatarUrl("avatarUrl");
        recruiter.setTotalCvView(10);
        return recruiter;
    }

    @Test
    void testSearchReportedUsers() {
        when(mockReportedRepository.searchReportedUsers(any(Pageable.class), eq("username"), eq("personReportName"),
                eq(Arrays.asList(1L)), eq(Arrays.asList(1L)))).thenReturn(new PageImpl<>(Arrays.asList()));
        final Page<ReportedUserResponse> result = reportedServiceImplUnderTest.searchReportedUsers(PageRequest.of(0, 1),
                "username", "personReportName", Arrays.asList(1L), Arrays.asList(1L));
    }

    @Test
    void testSearchReportedUsers_ReportedRepositoryReturnsNoItems() {
        when(mockReportedRepository.searchReportedUsers(any(Pageable.class), eq("username"), eq("personReportName"),
                eq(Arrays.asList(1L)), eq(Arrays.asList(1L)))).thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<ReportedUserResponse> result = reportedServiceImplUnderTest.searchReportedUsers(PageRequest.of(0, 1),
                "username", "personReportName", Arrays.asList(1L), Arrays.asList(1L));
    }

    @Test
    void testSearchReportedJob() {
        when(mockReportedRepository.searchReportedJob(any(Pageable.class), eq(localDateTime),
                eq(localDateTime), eq(localDateTime),
                eq(localDateTime), eq("jobName"))).thenReturn(new PageImpl<>(Arrays.asList()));
        final Page<ReportedJobResponse> result = reportedServiceImplUnderTest.searchReportedJob(PageRequest.of(0, 1),
                localDateTime, localDateTime,
                localDateTime, localDateTime, "jobName");
    }

    LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
    @Test
    void testSearchReportedJob_ReportedRepositoryReturnsNoItems() {
        when(mockReportedRepository.searchReportedJob(any(Pageable.class), eq(localDateTime),
                eq(localDateTime), eq(localDateTime),
                eq(localDateTime), eq("jobName")))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        final Page<ReportedJobResponse> result = reportedServiceImplUnderTest.searchReportedJob(PageRequest.of(0, 1),
                localDateTime, localDateTime,
                localDateTime, localDateTime, "jobName");
    }

    @Test
    void testReportJob() {
        final ReportJobRequest request = new ReportJobRequest();
        request.setJobId(1L);
        request.setReportReason("reportReason");
        request.setRelatedLink("relatedLink");
        request.setFullName("fullName");
        request.setPhone("phone");
        request.setUserAddress("userAddress");
        request.setUserEmail("userEmail");

        final Users users = users();
        when(mockUserService.getUserById(1L)).thenReturn(users);

        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);

        final Report report = report();
        when(mockModelMapper.map(any(Object.class), eq(Report.class))).thenReturn(report);

        final Recruiter recruiter = recruiter();
        when(mockRecruiterService.getRecruiterById(1L)).thenReturn(recruiter);

        final Report report1 = report();
        when(mockReportedRepository.save(any(Report.class))).thenReturn(report1);

        final Optional<Report> report2 = Optional.of(report);
        when(mockReportedRepository.findById(1L)).thenReturn(report2);
        final Report result = reportedServiceImplUnderTest.reportJob(request, 1L);
        verify(mockReportedRepository).save(any(Report.class));
    }

    @Test
    void testReportJob_UserServiceReturnsNull() {
        final ReportJobRequest request = new ReportJobRequest();
        request.setJobId(1L);
        request.setReportReason("reportReason");
        request.setRelatedLink("relatedLink");
        request.setFullName("fullName");
        request.setPhone("phone");
        request.setUserAddress("userAddress");
        request.setUserEmail("userEmail");

        when(mockUserService.getUserById(1L)).thenReturn(null);

        assertThatThrownBy(() -> reportedServiceImplUnderTest.reportJob(request, 1L))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    void testReportJob_JobServiceReturnsNull() {
        final ReportJobRequest request = new ReportJobRequest();
        request.setJobId(1L);
        request.setReportReason("reportReason");
        request.setRelatedLink("relatedLink");
        request.setFullName("fullName");
        request.setPhone("phone");
        request.setUserAddress("userAddress");
        request.setUserEmail("userEmail");

        final Users users = users();
        when(mockUserService.getUserById(1L)).thenReturn(users);

        when(mockJobService.getJobById(1L)).thenReturn(null);

        assertThatThrownBy(() -> reportedServiceImplUnderTest.reportJob(request, 1L))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    void testReportJob_ReportedRepositoryFindByIdReturnsAbsent() {
        final ReportJobRequest request = new ReportJobRequest();
        request.setJobId(1L);
        request.setReportReason("reportReason");
        request.setRelatedLink("relatedLink");
        request.setFullName("fullName");
        request.setPhone("phone");
        request.setUserAddress("userAddress");
        request.setUserEmail("userEmail");

        final Users users = users();
        when(mockUserService.getUserById(1L)).thenReturn(users);

        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);

        final Report report = report();
        when(mockModelMapper.map(any(Object.class), eq(Report.class))).thenReturn(report);

        final Recruiter recruiter = recruiter();
        when(mockRecruiterService.getRecruiterById(1L)).thenReturn(recruiter);

        final Report report1 = report();
        when(mockReportedRepository.save(any(Report.class))).thenReturn(report1);

        when(mockReportedRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> reportedServiceImplUnderTest.reportJob(request, 1L))
                .isInstanceOf(HiveConnectException.class);
        verify(mockReportedRepository).save(any(Report.class));
    }
}
