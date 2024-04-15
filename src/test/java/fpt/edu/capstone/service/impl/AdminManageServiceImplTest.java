package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.admin.LicenseApprovalResponse;
import fpt.edu.capstone.dto.banner.ApproveBannerRequest;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.BannerActiveRepository;
import fpt.edu.capstone.repository.JobRepository;
import fpt.edu.capstone.repository.ReportedRepository;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdminManageServiceImplTest {

    @Mock
    private RecruiterService mockRecruiterService;
    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private ReportedRepository mockReportedRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private JobService mockJobService;
    @Mock
    private ReportedService mockReportedService;
    @Mock
    private BannerActiveService mockBannerActiveService;
    @Mock
    private BannerService mockBannerService;
    @Mock
    private PaymentService mockPaymentService;
    @Mock
    private CompanyService mockCompanyService;
    @Mock
    private BannerActiveRepository mockBannerActiveRepository;
    @Mock
    private CandidateService mockCandidateService;
    @Mock
    private AdminService mockAdminService;
    @Mock
    private JobRepository mockJobRepository;

    @InjectMocks
    private AdminManageServiceImpl adminManageServiceImplUnderTest;
    
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
    Payment payment(){
        Payment payment = new Payment(1L, 1L, 1L, 1L, 1L, "transactionCode", 0, "description", "orderType", "bankCode", "command",
                "currCode", "local", LocalDateTime.of(2021, 10, 1, 0, 0, 0), false);
        return payment;
    }

    DetailPackage detailPackage(){
        DetailPackage detailPackage = new DetailPackage(1L, 1L, "detailName", 1231L, 1L, "timeExpired",
                "description", false, false, false, 0, false,false,false);
        return detailPackage;
    }
    
    BannerActive bannerActive(){
        BannerActive bannerActive = new BannerActive(1L, "bannerImageUrl", 1L, "displayPosition", false, "approvalStatus",
                        localDateTime);
        return bannerActive;
    }
    @Test
    public void testSearchLicenseApprovalForAdmin() {
        final List<Recruiter> recruiters = Arrays.asList(recruiter());
        when(mockRecruiterService.searchLicenseApprovalForAdmin("businessApprovalStatus",
                "additionalApprovalStatus")).thenReturn(recruiters);
        final List<LicenseApprovalResponse> result = adminManageServiceImplUnderTest.searchLicenseApprovalForAdmin(
                "businessApprovalStatus", "additionalApprovalStatus");
    }

    @Test
    public void testSearchLicenseApprovalForAdmin_RecruiterServiceReturnsNoItems() {
        when(mockRecruiterService.searchLicenseApprovalForAdmin("businessApprovalStatus",
                "additionalApprovalStatus")).thenReturn(Collections.emptyList());
        final List<LicenseApprovalResponse> result = adminManageServiceImplUnderTest.searchLicenseApprovalForAdmin(
                "businessApprovalStatus", "additionalApprovalStatus");
        assertThat(result).isEqualTo(Collections.emptyList());
    }
    
    private Banner banner(){
        Banner banner = new Banner(1L, 1L, 1L, 1L, "timeExpired", "packageName", "description", "image", false,
                false, false, false, false, false, false, false);
        return banner;
    }
    @Test
    public void testGetBannerOfRecruiterForAdmin() {
        final Page<BannerActive> bannerActives = new PageImpl<>(Arrays.asList(bannerActive()));
        when(mockBannerActiveService.getAllBannerForApproval(any(Pageable.class), eq("screenName"),
                eq(localDateTime), eq(localDateTime)))
                .thenReturn(bannerActives);

        final Payment payment = payment();
        when(mockPaymentService.findById(1L)).thenReturn(payment);

        final Banner banner = banner();
        when(mockBannerService.findById(1L)).thenReturn(banner);

        final Optional<Recruiter> recruiter = Optional.of(recruiter());
        when(mockRecruiterService.findById(1L)).thenReturn(recruiter);

        final Company company1 = new Company();
        company1.setCreatedAt(localDateTime);
        company1.setUpdatedAt(localDateTime);
        company1.setId(1L);
        company1.setFieldWork("fieldWork");
        company1.setName("companyName");
        company1.setEmail("email");
        company1.setPhone("phone");
        company1.setDescription("description");
        company1.setWebsite("website");
        company1.setNumberEmployees("numberEmployees");
        company1.setAddress("address");
        company1.setTaxCode("taxCode");
        company1.setIsDeleted(0);
        company1.setMapUrl("mapUrl");
        company1.setCreatorId(1L);
        final Optional<Company> company = Optional.of(company1);
        when(mockCompanyService.findById(1L)).thenReturn(company);

        final ResponseDataPagination result = adminManageServiceImplUnderTest.getBannerOfRecruiterForAdmin(1, 10,
                "screenName", localDateTime, localDateTime);
    }
    LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
    @Test
    public void testSearchReportedJob() {
        when(mockReportedService.searchReportedJob(any(Pageable.class), eq(localDateTime),
                eq(localDateTime), eq(localDateTime),
                eq(localDateTime), eq("jobName"))).thenReturn(new PageImpl<>(Arrays.asList()));
        final ResponseDataPagination result = adminManageServiceImplUnderTest.searchReportedJob(1, 10,
                localDateTime, localDateTime,
                localDateTime, localDateTime, "jobName");
    }

    @Test
    public void testSearchReportedJob_ReportedServiceReturnsNoItems() {
        when(mockReportedService.searchReportedJob(any(Pageable.class), eq(localDateTime),
                eq(localDateTime), eq(localDateTime),
                eq(localDateTime), eq("jobName")))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final ResponseDataPagination result = adminManageServiceImplUnderTest.searchReportedJob(1, 10,
                localDateTime, localDateTime,
                localDateTime, localDateTime, "jobName");
    }

    @Test
    public void testApproveBanner() {
        final ApproveBannerRequest request = new ApproveBannerRequest(1L, "Pending");
        final BannerActive bannerActive = new BannerActive(1L, "bannerImageUrl", 1L, "displayPosition", false,
                "approvalStatus", localDateTime);
        when(mockBannerActiveService.findById(1L)).thenReturn(bannerActive);
        final BannerActive bannerActive1 = new BannerActive(1L, "bannerImageUrl", 1L, "displayPosition", false,
                "approvalStatus", localDateTime);
        when(mockBannerActiveRepository.save(any(BannerActive.class))).thenReturn(bannerActive1);
        adminManageServiceImplUnderTest.approveBanner(request);
        verify(mockBannerActiveRepository).save(any(BannerActive.class));
    }

    @Test
    public void testGetBannerOfRecruiterForAdmin_BannerActiveServiceReturnsNoItems() {
        when(mockBannerActiveService.getAllBannerForApproval(any(Pageable.class),eq("screenName"),any(),any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final Payment payment = payment();
        when(mockPaymentService.findById(1L)).thenReturn(payment);
        final Banner banner = new Banner();
        when(mockBannerService.findById(1L)).thenReturn(banner);
        final Optional<Recruiter> recruiter = Optional.of(recruiter());
        when(mockRecruiterService.findById(1L)).thenReturn(recruiter);
        final Company company1 = new Company();
        company1.setCreatedAt(localDateTime);
        company1.setUpdatedAt(localDateTime);
        company1.setId(1L);
        company1.setFieldWork("fieldWork");
        company1.setName("companyName");
        company1.setEmail("email");
        company1.setPhone("phone");
        company1.setDescription("description");
        company1.setWebsite("website");
        company1.setNumberEmployees("numberEmployees");
        company1.setAddress("address");
        company1.setTaxCode("taxCode");
        company1.setIsDeleted(0);
        company1.setMapUrl("mapUrl");
        company1.setCreatorId(1L);
        final Optional<Company> company = Optional.of(company1);
        when(mockCompanyService.findById(1L)).thenReturn(company);
        final ResponseDataPagination result = adminManageServiceImplUnderTest.getBannerOfRecruiterForAdmin(1, 10,null,null,null);
    }

    @Test
    public void testGetBannerOfRecruiterForAdmin_RecruiterServiceReturnsAbsent() {
        final Page<BannerActive> bannerActives = new PageImpl<>(Arrays.asList(bannerActive()));
        when(mockBannerActiveService.getAllBannerForApproval(any(Pageable.class),eq("screenName"),any(),any())).thenReturn(bannerActives);
        final Payment payment = payment();
        when(mockPaymentService.findById(1L)).thenReturn(payment);
        final Banner banner = banner();
        when(mockBannerService.findById(1L)).thenReturn(banner);
        when(mockRecruiterService.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adminManageServiceImplUnderTest.getBannerOfRecruiterForAdmin(0, 0,null,null,null))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testGetBannerOfRecruiterForAdmin_CompanyServiceReturnsAbsent() {
        final Page<BannerActive> bannerActives = new PageImpl<>(Arrays.asList(bannerActive()));
        when(mockBannerActiveService.getAllBannerForApproval(any(Pageable.class),eq(""),any(),any())).thenReturn(bannerActives);
        final Payment payment = payment();
        when(mockPaymentService.findById(1L)).thenReturn(payment);
        final Banner banner = banner();
        when(mockBannerService.findById(1L)).thenReturn(banner);
        final Optional<Recruiter> recruiter = Optional.of(recruiter());
        when(mockRecruiterService.findById(1L)).thenReturn(recruiter);

        when(mockCompanyService.findById(1L)).thenReturn(Optional.empty());
        final ResponseDataPagination result = adminManageServiceImplUnderTest.getBannerOfRecruiterForAdmin(1, 10,null,null,null);
    }

    @Test
    public void testSearchUsersForAdmin() {
        when(mockRecruiterService.searchRecruitersForAdmin(any(Pageable.class), eq("username"),
                eq("email"), eq("fullName"), eq(1L),  eq(false))).thenReturn(new PageImpl<>(Arrays.asList()));
        when(mockCandidateService.searchCandidatesForAdmin(any(Pageable.class), eq("username"),
                eq("email"), eq("fullName"), eq(1L), eq(false))).thenReturn(new PageImpl<>(Arrays.asList()));
        when(mockAdminService.searchAdmins(any(Pageable.class), eq("username"), eq("email"), eq("fullName"), eq(1L),  eq(false)))
                .thenReturn(new PageImpl<>(Arrays.asList()));
        final ResponseDataPagination result = adminManageServiceImplUnderTest.searchUsersForAdmin("Recruiter", 1, 10,
                "username", "email", "", 1L, false);
    }

    @Test
    public void testSearchUsersForAdmin_RecruiterServiceReturnsNoItems() {
        when(mockRecruiterService.searchRecruitersForAdmin(any(Pageable.class), eq("username"),
                eq("email"), eq("fullName"), 1L,  eq(false))).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(mockCandidateService.searchCandidatesForAdmin(any(Pageable.class), eq("username"),
                eq("email"), eq("fullName"), eq(1L),  eq(false))).thenReturn(new PageImpl<>(Arrays.asList()));
        when(mockAdminService.searchAdmins(any(Pageable.class), eq("username"), eq("email"), eq("fullName"), eq(1L),  eq(false)))
                .thenReturn(new PageImpl<>(Arrays.asList()));
        final ResponseDataPagination result = adminManageServiceImplUnderTest.searchUsersForAdmin("selectTab", 1, 10,
                "username", "email", "fullName", 1L, false);
    }

    @Test
    public void testSearchUsersForAdmin_CandidateServiceReturnsNoItems() {
        when(mockRecruiterService.searchRecruitersForAdmin(any(Pageable.class), eq("username"),
                eq("email"), eq("fullName"), eq(1L),  eq(false))).thenReturn(new PageImpl<>(Arrays.asList()));
        when(mockCandidateService.searchCandidatesForAdmin(any(Pageable.class), eq("username"),
                eq("email"), eq("fullName"), eq(1L),  eq(false))).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(mockAdminService.searchAdmins(any(Pageable.class), eq("username"), eq("email"), eq("fullName"), eq(1L),  eq(false)))
                .thenReturn(new PageImpl<>(Arrays.asList()));
//        final ResponseDataPagination result = adminManageServiceImplUnderTest.searchUsersForAdmin("Recruiter", 1, 10,
//                "username", "email", "", 1L, false);
    }

    @Test
    public void testSearchUsersForAdmin_AdminServiceReturnsNoItems() {
        when(mockRecruiterService.searchRecruitersForAdmin(any(Pageable.class), eq("username"),
                eq("email"), eq("fullName"), eq(1L),eq(false))).thenReturn(new PageImpl<>(Arrays.asList()));
        when(mockCandidateService.searchCandidatesForAdmin(any(Pageable.class), eq("username"),
                eq("email"), eq("fullName"), eq(1L),  eq(false))).thenReturn(new PageImpl<>(Arrays.asList()));
        when(mockAdminService.searchAdmins(any(Pageable.class), eq("username"), eq("email"), eq("fullName"), eq(1L),  eq(false)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final ResponseDataPagination result = adminManageServiceImplUnderTest.searchUsersForAdmin("Recruiter", 1, 10,
                "username", "email", "", 1L, false);
    }

    @Test
    public void testSearchReportedUsers() {
        final List<Users> users = Arrays.asList(
                new Users(1L, "username", "password", "email", "phone", 1L, 0, localDateTime,
                        false, false, false, false, "avatar", "resetPasswordToken", false, "lockReason"));
        when(mockUserService.findAll()).thenReturn(users);
        when(mockReportedService.searchReportedUsers(any(Pageable.class), eq("username"), eq("personReportName"),
                eq(Arrays.asList(1L)), eq(Arrays.asList(1L)))).thenReturn(new PageImpl<>(Arrays.asList()));
        final ResponseDataPagination result = adminManageServiceImplUnderTest.searchReportedUsers(1, 10, "username",
                "personReportName", Arrays.asList(1L), Arrays.asList(1L));
    }

    @Test
    public void testSearchReportedUsers_UserServiceReturnsNoItems() {
        when(mockUserService.findAll()).thenReturn(Collections.emptyList());
        when(mockReportedService.searchReportedUsers(any(Pageable.class), eq("username"), eq("personReportName"),
                eq(Arrays.asList(1L)), eq(Arrays.asList(1L)))).thenReturn(new PageImpl<>(Arrays.asList()));
        final ResponseDataPagination result = adminManageServiceImplUnderTest.searchReportedUsers(1, 10, "username",
                "personReportName", Arrays.asList(1L), Arrays.asList(1L));
    }

    @Test
    public void testSearchReportedUsers_ReportedServiceReturnsNoItems() {
        final List<Users> users = Arrays.asList(
                new Users(1L, "username", "password", "email", "phone", 1L, 0, localDateTime,
                        false, false, false, false, "avatar", "resetPasswordToken", false, "lockReason"));
        when(mockUserService.findAll()).thenReturn(users);
        when(mockReportedService.searchReportedUsers(any(Pageable.class), eq("username"), eq("personReportName"),
                eq(Arrays.asList(1L)), eq(Arrays.asList(1L)))).thenReturn(new PageImpl<>(Collections.emptyList()));
        final ResponseDataPagination result = adminManageServiceImplUnderTest.searchReportedUsers(1, 10, "username",
                "personReportName", Arrays.asList(1L), Arrays.asList(1L));
    }

    @Test
    public void testApproveReportedJob() {
        final Optional<Report> report = Optional.of(new Report());
        when(mockReportedRepository.findById(1L)).thenReturn(report);
        final Job job = new Job();
        when(mockJobService.getJobById(1L)).thenReturn(job);
        final Job job1 = new Job();
        when(mockJobRepository.save(any(Job.class))).thenReturn(job1);
        final Report report1 = new Report(1L, 1L, "reportReason", 1L,  "relatedLink", "approvalStatus", "fullName",
                "phone", "userAddress", "userEmail", "reportType", 1L);
        when(mockReportedRepository.save(any(Report.class))).thenReturn(report1);
        final String result = adminManageServiceImplUnderTest.approveReportedJob("Pending", 1L);
        assertThat(result).isEqualTo("Hủy báo cáo tin tuyển dụng thành công");
        verify(mockJobRepository).save(any(Job.class));
        verify(mockReportedRepository).save(any(Report.class));
    }

    @Test
    public void testApproveReportedJob_ReportedRepositoryFindByIdReturnsAbsent() {
        when(mockReportedRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adminManageServiceImplUnderTest.approveReportedJob("approvalStatus", 1L))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testApproveReportedJob_JobServiceReturnsNull() {
        final Optional<Report> report = Optional.of(
                new Report(1L, 1L, "reportReason", 1L, "relatedLink", "approvalStatus", "fullName", "phone",
                        "userAddress", "userEmail", "reportType", 1L));
        when(mockReportedRepository.findById(1L)).thenReturn(report);

        when(mockJobService.getJobById(1L)).thenReturn(null);
        assertThatThrownBy(() -> adminManageServiceImplUnderTest.approveReportedJob("approvalStatus", 1L))
                .isInstanceOf(HiveConnectException.class);
    }
}
