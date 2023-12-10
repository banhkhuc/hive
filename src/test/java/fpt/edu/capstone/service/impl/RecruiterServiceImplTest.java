//package fpt.edu.capstone.service.impl;
//
//import fpt.edu.capstone.common.user.GooglePojo;
//import fpt.edu.capstone.dto.AppliedJobByRecruiterResponse;
//import fpt.edu.capstone.dto.UploadFileRequest;
//import fpt.edu.capstone.dto.admin.user.RecruiterManageResponse;
//import fpt.edu.capstone.dto.recruiter.ApprovalLicenseRequest;
//import fpt.edu.capstone.entity.Company;
//import fpt.edu.capstone.entity.Recruiter;
//import fpt.edu.capstone.entity.Users;
//import fpt.edu.capstone.exception.HiveConnectException;
//import fpt.edu.capstone.repository.CompanyRepository;
//import fpt.edu.capstone.repository.RecruiterRepository;
//import fpt.edu.capstone.repository.UserRepository;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class RecruiterServiceImplTest {
//
//    @Mock
//    private RecruiterRepository mockRecruiterRepository;
//    @Mock
//    private CompanyRepository mockCompanyRepository;
//    @Mock
//    private UserRepository mockUserRepository;
//    @Mock
//    private AmazonS3ClientService mockAmazonS3ClientService;
//
//    private RecruiterServiceImpl recruiterServiceImplUnderTest;
//
//    @Before
//    public void setUp() {
//        recruiterServiceImplUnderTest = new RecruiterServiceImpl(mockRecruiterRepository, mockCompanyRepository,
//                mockUserRepository, mockAmazonS3ClientService);
//    }
//
//    private Recruiter recruiter(){
//        Recruiter recruiter = new Recruiter();
//        recruiter.setId(1L);
//        recruiter.setCompanyId(1L);
//        recruiter.setCompanyName("companyName");
//        recruiter.setFullName("fullName");
//        recruiter.setVerifyAccount(true);
//        recruiter.setGender(false);
//        recruiter.setPosition("HR");
//        recruiter.setLinkedinAccount("linkedinAccount");
//        recruiter.setBusinessLicense("businessLicense");
//        recruiter.setAdditionalLicense("additionalLicense");
//        recruiter.setBusinessLicenseUrl("businessLicenseUrl");
//        recruiter.setAdditionalLicenseUrl("additionalLicenseUrl");
//        recruiter.setUserId(1L);
//        recruiter.setDeleted(false);
//        recruiter.setCompanyAddress("companyAddress");
//        recruiter.setBusinessLicenseApprovalStatus("businessLicenseApprovalStatus");
//        recruiter.setAvatarUrl("avatarUrl");
//        recruiter.setTotalCvView(10);
//        return recruiter;
//    }
//
//    private Users users(){
//        Users users = new Users();
//        users.setId(1L);
//        users.setUsername("username");
//        users.setPassword("password");
//        users.setEmail("email");
//        users.setPhone("0967445450");
//        users.setRoleId(1L);
//        users.setIsDeleted(0);
//        users.setLastLoginTime(LocalDateTime.now());
//        users.setVerifiedEmail(false);
//        users.setVerifiedPhone(false);
//        users.setActive(true);
//        users.setLocked(false);
//        users.setAvatar("avatar");
//        users.setResetPasswordToken("setResetPasswordToken");
//        users.setGoogle(false);
//        return users;
//    }
//
//    private Company company(){
//        Company company1 = new Company();
//        company1.setId(1L);
//        company1.setFieldWork("fieldWork");
//        company1.setName("name");
//        company1.setEmail("email");
//        company1.setPhone("phone");
//        company1.setDescription("description");
//        company1.setWebsite("website");
//        company1.setNumberEmployees("numberEmployees");
//        company1.setAddress("address");
//        company1.setTaxCode("taxCode");
//        company1.setIsDeleted(0);
//        company1.setMapUrl("mapUrl");
//        company1.setCreatorId(1L);
//        company1.setLocked(false);
//        return company1;
//    }
//
//    @Test
//    public void testGetRecruiterByUserId() {
//        final Recruiter recruiter = recruiter();
//        when(mockRecruiterRepository.getRecruiterByUserId(1L)).thenReturn(recruiter);
//        final Recruiter result = recruiterServiceImplUnderTest.getRecruiterByUserId(1L);
//    }
//
//    @Test
//    public void testExistById() {
//        when(mockRecruiterRepository.existsById(1L)).thenReturn(false);
//        final boolean result = recruiterServiceImplUnderTest.existById(1L);
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    public void testGetRecruiterById() {
//        final Recruiter recruiter = recruiter();
//        when(mockRecruiterRepository.getById(1L)).thenReturn(recruiter);
//        final Recruiter result = recruiterServiceImplUnderTest.getRecruiterById(1L);
//    }
//
//    @Test
//    public void testFindRecruiterByUserId() {
//        final Optional<Recruiter> recruiter = Optional.of(recruiter());
//        when(mockRecruiterRepository.findByUserId(1L)).thenReturn(recruiter);
//        final Optional<Recruiter> result = recruiterServiceImplUnderTest.findRecruiterByUserId(1L);
//    }
//
//    @Test
//    public void testFindRecruiterByUserId_RecruiterRepositoryReturnsAbsent() {
//        when(mockRecruiterRepository.findByUserId(1L)).thenReturn(Optional.empty());
//        final Optional<Recruiter> result = recruiterServiceImplUnderTest.findRecruiterByUserId(1L);
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    public void testFindById() {
//        final Optional<Recruiter> recruiter = Optional.of(recruiter());
//        when(mockRecruiterRepository.findById(1L)).thenReturn(recruiter);
//        final Optional<Recruiter> result = recruiterServiceImplUnderTest.findById(1L);
//    }
//
//    @Test
//    public void testFindById_RecruiterRepositoryReturnsAbsent() {
//        when(mockRecruiterRepository.findById(1L)).thenReturn(Optional.empty());
//        final Optional<Recruiter> result = recruiterServiceImplUnderTest.findById(1L);
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    public void testInsertRecruiter() {
//        final Recruiter recruiter = recruiter();
//        when(mockRecruiterRepository.save(any(Recruiter.class))).thenReturn(recruiter);
//        final Recruiter result = recruiterServiceImplUnderTest.insertRecruiter(1L);
//    }
//
//    @Test
//    public void testGetListAppliedByForRecruiter() {
//        when(mockRecruiterRepository.getListAppliedByForRecruiter(1L)).thenReturn(Arrays.asList());
//        final List<AppliedJobByRecruiterResponse> result = recruiterServiceImplUnderTest.getListAppliedByForRecruiter(
//                1L);
//    }
//
//    @Test
//    public void testGetListAppliedByForRecruiter_RecruiterRepositoryReturnsNoItems() {
//        when(mockRecruiterRepository.getListAppliedByForRecruiter(1L)).thenReturn(Collections.emptyList());
//        final List<AppliedJobByRecruiterResponse> result = recruiterServiceImplUnderTest.getListAppliedByForRecruiter(1L);
//        assertThat(result).isEqualTo(Collections.emptyList());
//    }
//
//    @Test
//    public void testUpdateCompany() {
//        recruiterServiceImplUnderTest.updateCompany(1L, 1L);
//        verify(mockRecruiterRepository).updateCompany(1L, 1L);
//    }
//
//    @Test
//    public void testGetRecruiterByCompanyId() {
//        final Page<Recruiter> recruiterPage = new PageImpl<>(Arrays.asList(recruiter()));
//        when(mockRecruiterRepository.getRecruiterByCompanyId(eq(1L), any(Pageable.class))).thenReturn(recruiterPage);
//        final Page<Recruiter> result = recruiterServiceImplUnderTest.getRecruiterByCompanyId(1L, 1L, 1L);
//    }
//
//    @Test
//    public void testGetRecruiterByCompanyId_RecruiterRepositoryReturnsNoItems() {
//        when(mockRecruiterRepository.getRecruiterByCompanyId(eq(1L), any(Pageable.class)))
//                .thenReturn(new PageImpl<>(Collections.emptyList()));
//        final Page<Recruiter> result = recruiterServiceImplUnderTest.getRecruiterByCompanyId(1L, 1L, 1L);
//    }
//
//    @Test
//    public void testSearchRecruitersForAdmin() {
//        when(mockRecruiterRepository.searchRecruitersForAdmin(any(Pageable.class), eq("username"),
//                eq("email"))).thenReturn(new PageImpl<>(Arrays.asList()));
//        final Page<RecruiterManageResponse> result = recruiterServiceImplUnderTest.searchRecruitersForAdmin(
//                PageRequest.of(1, 10), "username", "email");
//    }
//
//    @Test
//    public void testSearchRecruitersForAdmin_RecruiterRepositoryReturnsNoItems() {
//        when(mockRecruiterRepository.searchRecruitersForAdmin(any(Pageable.class), eq("username"),
//                eq("email"))).thenReturn(new PageImpl<>(Collections.emptyList()));
//        final Page<RecruiterManageResponse> result = recruiterServiceImplUnderTest.searchRecruitersForAdmin(
//                PageRequest.of(1, 10), "username", "email");
//    }
//
//    @Test
//    public void testUploadLicense() throws Exception {
//        final MultipartFile businessMultipartFile = null;
//        final MultipartFile additionalMultipartFile = null;
//        final Optional<Recruiter> recruiter = Optional.of(recruiter());
//        when(mockRecruiterRepository.findById(1L)).thenReturn(recruiter);
//        when(mockAmazonS3ClientService.uploadFileAmazonS3(any(UploadFileRequest.class),
//                any(MultipartFile.class))).thenReturn("businessLicense");
//        final Recruiter recruiter1 = recruiter();
//        when(mockRecruiterRepository.save(any(Recruiter.class))).thenReturn(recruiter1);
//        final Recruiter result = recruiterServiceImplUnderTest.uploadLicense(1L, businessMultipartFile,
//                additionalMultipartFile);
//        verify(mockRecruiterRepository).save(any(Recruiter.class));
//    }
//
//    @Test
//    public void testUploadLicense_RecruiterRepositoryFindByIdReturnsAbsent() {
//        final MultipartFile businessMultipartFile = null;
//        final MultipartFile additionalMultipartFile = null;
//        when(mockRecruiterRepository.findById(1L)).thenReturn(Optional.empty());
//        assertThatThrownBy(() -> recruiterServiceImplUnderTest.uploadLicense(1L, businessMultipartFile,
//                additionalMultipartFile)).isInstanceOf(HiveConnectException.class);
//    }
//
//    @Test
//    public void testUploadLicense_AmazonS3ClientServiceThrowsException() throws Exception {
//        final MultipartFile businessMultipartFile = null;
//        final MultipartFile additionalMultipartFile = null;
//        final Optional<Recruiter> recruiter = Optional.of(recruiter());
//        when(mockRecruiterRepository.findById(1L)).thenReturn(recruiter);
//        when(mockAmazonS3ClientService.uploadFileAmazonS3(any(UploadFileRequest.class),
//                any(MultipartFile.class))).thenThrow(Exception.class);
//        assertThatThrownBy(() -> recruiterServiceImplUnderTest.uploadLicense(1L, businessMultipartFile,
//                additionalMultipartFile)).isInstanceOf(Exception.class);
//    }
//
//    @Test
//    public void testSearchLicenseApprovalForAdmin() {
//        final List<Recruiter> recruiters = Arrays.asList(recruiter());
//        when(mockRecruiterRepository.searchLicenseApprovalForAdmin("businessApprovalStatus",
//                "additionalApprovalStatus")).thenReturn(recruiters);
//        final List<Recruiter> result = recruiterServiceImplUnderTest.searchLicenseApprovalForAdmin(
//                "businessApprovalStatus", "additionalApprovalStatus");
//    }
//
//    @Test
//    public void testSearchLicenseApprovalForAdmin_RecruiterRepositoryReturnsNoItems() {
//        when(mockRecruiterRepository.searchLicenseApprovalForAdmin("businessApprovalStatus",
//                "additionalApprovalStatus")).thenReturn(Collections.emptyList());
//        final List<Recruiter> result = recruiterServiceImplUnderTest.searchLicenseApprovalForAdmin(
//                "businessApprovalStatus", "additionalApprovalStatus");
//        assertThat(result).isEqualTo(Collections.emptyList());
//    }
//
//    @Test
//    public void testApproveLicense() {
//        final ApprovalLicenseRequest request = new ApprovalLicenseRequest();
//        request.setUserId(1L);
//        request.setType("type");
//        request.setApprovalStatus("approvalStatus");
//        final Recruiter recruiter = recruiter();
//        when(mockRecruiterRepository.getRecruiterByUserId(1L)).thenReturn(recruiter);
//        final Recruiter recruiter1 = recruiter();
//        when(mockRecruiterRepository.save(any(Recruiter.class))).thenReturn(recruiter1);
//        final Recruiter result = recruiterServiceImplUnderTest.approveLicense(request);
//        verify(mockRecruiterRepository).save(any(Recruiter.class));
//    }
//
//    @Test
//    public void testApproveLicense_RecruiterRepositoryGetRecruiterByUserIdReturnsNull() {
//        final ApprovalLicenseRequest request = new ApprovalLicenseRequest();
//        request.setUserId(1L);
//        request.setType("type");
//        request.setApprovalStatus("approvalStatus");
//        when(mockRecruiterRepository.getRecruiterByUserId(1L)).thenReturn(null);
//        assertThatThrownBy(() -> recruiterServiceImplUnderTest.approveLicense(request))
//                .isInstanceOf(HiveConnectException.class);
//    }
//
//    @Test
//    public void testGetById() {
//        final Recruiter recruiter = recruiter();
//        when(mockRecruiterRepository.getById(1L)).thenReturn(recruiter);
//        final Recruiter result = recruiterServiceImplUnderTest.getById(1L);
//    }
//
//    @Test
//    public void testUpdateTotalCvView() {
//        recruiterServiceImplUnderTest.updateTotalCvView(1L, 1L);
//        verify(mockRecruiterRepository).updateTotalCvView(1L, 1L);
//    }
//
//    @Test
//    public void testInsertGoogleRecruiter() {
//        final GooglePojo googlePojo = new GooglePojo();
//        googlePojo.setEmail("email");
//        googlePojo.setName("fullName");
//        googlePojo.setPicture("avatarUrl");
//
//        final Users user = users();
//        final Recruiter recruiter = recruiter();
//        when(mockRecruiterRepository.save(any(Recruiter.class))).thenReturn(recruiter);
//        recruiterServiceImplUnderTest.insertGoogleRecruiter(googlePojo, user);
//        verify(mockRecruiterRepository).save(any(Recruiter.class));
//    }
//
//    @Test
//    public void testGetTotalViewCV() {
//        when(mockRecruiterRepository.getTotalViewCV(1L)).thenReturn(0);
//        final Integer result = recruiterServiceImplUnderTest.getTotalViewCV(1L);
//        assertThat(result).isEqualTo(0);
//    }
//}
package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.common.user.GooglePojo;
import fpt.edu.capstone.dto.AppliedJobByRecruiterResponse;
import fpt.edu.capstone.dto.admin.user.RecruiterManageResponse;
import fpt.edu.capstone.dto.recruiter.ApprovalLicenseRequest;
import fpt.edu.capstone.entity.Company;
import fpt.edu.capstone.entity.Recruiter;
import fpt.edu.capstone.entity.Users;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.CompanyRepository;
import fpt.edu.capstone.repository.RecruiterRepository;
import fpt.edu.capstone.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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
public class RecruiterServiceImplTest {

    @Mock
    private RecruiterRepository mockRecruiterRepository;
    @Mock
    private CompanyRepository mockCompanyRepository;
    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private DinaryServiceImpl dinaryServicei;

    private RecruiterServiceImpl recruiterServiceImplUnderTest;

    @Before
    public void setUp() {
        recruiterServiceImplUnderTest = new RecruiterServiceImpl(mockRecruiterRepository, dinaryServicei);
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

    private Company company(){
        Company company1 = new Company();
        company1.setId(1L);
        company1.setFieldWork("fieldWork");
        company1.setName("name");
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
        company1.setLocked(false);
        return company1;
    }

    @Test
    public void testGetRecruiterByUserId() {
        final Recruiter recruiter = recruiter();
        when(mockRecruiterRepository.getRecruiterByUserId(1L)).thenReturn(recruiter);
        final Recruiter result = recruiterServiceImplUnderTest.getRecruiterByUserId(1L);
    }

    @Test
    public void testExistById() {
        when(mockRecruiterRepository.existsById(1L)).thenReturn(false);
        final boolean result = recruiterServiceImplUnderTest.existById(1L);
        assertThat(result).isFalse();
    }

    @Test
    public void testGetRecruiterById() {
        final Recruiter recruiter = recruiter();
        when(mockRecruiterRepository.getById(1L)).thenReturn(recruiter);
        final Recruiter result = recruiterServiceImplUnderTest.getRecruiterById(1L);
    }

    @Test
    public void testFindRecruiterByUserId() {
        final Optional<Recruiter> recruiter = Optional.of(recruiter());
        when(mockRecruiterRepository.findByUserId(1L)).thenReturn(recruiter);
        final Optional<Recruiter> result = recruiterServiceImplUnderTest.findRecruiterByUserId(1L);
    }

    @Test
    public void testFindRecruiterByUserId_RecruiterRepositoryReturnsAbsent() {
        when(mockRecruiterRepository.findByUserId(1L)).thenReturn(Optional.empty());
        final Optional<Recruiter> result = recruiterServiceImplUnderTest.findRecruiterByUserId(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testFindById() {
        final Optional<Recruiter> recruiter = Optional.of(recruiter());
        when(mockRecruiterRepository.findById(1L)).thenReturn(recruiter);
        final Optional<Recruiter> result = recruiterServiceImplUnderTest.findById(1L);
    }

    @Test
    public void testFindById_RecruiterRepositoryReturnsAbsent() {
        when(mockRecruiterRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<Recruiter> result = recruiterServiceImplUnderTest.findById(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testInsertRecruiter() {
        final Recruiter recruiter = recruiter();
        when(mockRecruiterRepository.save(any(Recruiter.class))).thenReturn(recruiter);
        final Recruiter result = recruiterServiceImplUnderTest.insertRecruiter(1L);
    }

    @Test
    public void testInsertRecruiterForRegister() {
        final Recruiter recruiter = recruiter();
        when(mockRecruiterRepository.save(any(Recruiter.class))).thenReturn(recruiter);
        final Recruiter result = recruiterServiceImplUnderTest.insertRecruiterForRegister(1L, "Nam Nguyen");
    }

    @Test
    public void testGetListAppliedByForRecruiter() {
        when(mockRecruiterRepository.getListAppliedByForRecruiter(1L)).thenReturn(Arrays.asList());
        final List<AppliedJobByRecruiterResponse> result = recruiterServiceImplUnderTest.getListAppliedByForRecruiter(
                1L);
    }

    @Test
    public void testGetListAppliedByForRecruiter_RecruiterRepositoryReturnsNoItems() {
        when(mockRecruiterRepository.getListAppliedByForRecruiter(1L)).thenReturn(Collections.emptyList());
        final List<AppliedJobByRecruiterResponse> result = recruiterServiceImplUnderTest.getListAppliedByForRecruiter(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testUpdateCompany() {
        recruiterServiceImplUnderTest.updateCompany(1L, 1L);
        verify(mockRecruiterRepository).updateCompany(1L, 1L);
    }

    @Test
    public void testGetRecruiterByCompanyId() {
        final Page<Recruiter> recruiterPage = new PageImpl<>(Arrays.asList(recruiter()));
        when(mockRecruiterRepository.getRecruiterByCompanyId(eq(1L),eq(""), eq(""), eq(""), any(Pageable.class))).thenReturn(recruiterPage);
        final Page<Recruiter> result = recruiterServiceImplUnderTest.getRecruiterByCompanyId(1L, 11L, 1L,"","","");
    }

    @Test
    public void testGetRecruiterByCompanyId_RecruiterRepositoryReturnsNoItems() {
        when(mockRecruiterRepository.getRecruiterByCompanyId(eq(1L),eq(""), eq(""), eq(""), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<Recruiter> result = recruiterServiceImplUnderTest.getRecruiterByCompanyId(1L, 11L, 1L,"","","");
    }

    @Test
    public void testSearchRecruitersForAdmin() {
        when(mockRecruiterRepository.searchRecruitersForAdmin(any(Pageable.class), eq("username"),
                eq("email"), eq("fullName"), eq(1L), eq(false))).thenReturn(new PageImpl<>(Arrays.asList()));
        final Page<RecruiterManageResponse> result = recruiterServiceImplUnderTest.searchRecruitersForAdmin(
                PageRequest.of(1, 10), "username", "email", "", 1L, false);
    }

    @Test
    public void testSearchRecruitersForAdmin_RecruiterRepositoryReturnsNoItems() {
        when(mockRecruiterRepository.searchRecruitersForAdmin(any(Pageable.class), eq("username"),
                eq("email"), eq("fullName"), eq(1L), eq(false))).thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<RecruiterManageResponse> result = recruiterServiceImplUnderTest.searchRecruitersForAdmin(
                PageRequest.of(1, 10), "username", "email", "", 1L, false);
    }

//    @Test
//    public void testUploadLicense() throws Exception {
//        final MultipartFile businessMultipartFile = new MockMultipartFile("sourceFile.tmp", "Hello World".getBytes());
//        final MultipartFile additionalMultipartFile = new MockMultipartFile("sourceFile.tmp", "Hello World".getBytes());
//        final Optional<Recruiter> recruiter = Optional.of(recruiter());
//        when(mockRecruiterRepository.findById(1L)).thenReturn(recruiter);
//        when(mockAmazonS3ClientService.uploadFileAmazonS3(any(UploadFileRequest.class),
//                any(MultipartFile.class))).thenReturn("businessLicense");
//        final Recruiter recruiter1 = recruiter();
//        when(mockRecruiterRepository.save(any(Recruiter.class))).thenReturn(recruiter1);
//        final Recruiter result = recruiterServiceImplUnderTest.uploadLicense(1L, businessMultipartFile,
//                additionalMultipartFile);
//        verify(mockRecruiterRepository).save(any(Recruiter.class));
//    }

    @Test
    public void testUploadLicense_RecruiterRepositoryFindByIdReturnsAbsent() {
        final MultipartFile businessMultipartFile = null;
        final MultipartFile additionalMultipartFile = null;
        when(mockRecruiterRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> recruiterServiceImplUnderTest.uploadLicense(1L, businessMultipartFile,
                additionalMultipartFile)).isInstanceOf(HiveConnectException.class);
    }

//    @Test
//    public void testUploadLicense_AmazonS3ClientServiceThrowsException() throws Exception {
//        final MultipartFile businessMultipartFile = null;
//        final MultipartFile additionalMultipartFile = null;
//        final Optional<Recruiter> recruiter = Optional.of(recruiter());
//        when(mockRecruiterRepository.findById(1L)).thenReturn(recruiter);
//        when(mockAmazonS3ClientService.uploadFileAmazonS3(any(UploadFileRequest.class),
//                any(MultipartFile.class))).thenThrow(Exception.class);
//        assertThatThrownBy(() -> recruiterServiceImplUnderTest.uploadLicense(1L, businessMultipartFile,
//                additionalMultipartFile)).isInstanceOf(Exception.class);
//    }

    @Test
    public void testSearchLicenseApprovalForAdmin() {
        final List<Recruiter> recruiters = Arrays.asList(recruiter());
        when(mockRecruiterRepository.searchLicenseApprovalForAdmin("businessApprovalStatus",
                "additionalApprovalStatus")).thenReturn(recruiters);
        final List<Recruiter> result = recruiterServiceImplUnderTest.searchLicenseApprovalForAdmin(
                "businessApprovalStatus", "additionalApprovalStatus");
    }

    @Test
    public void testSearchLicenseApprovalForAdmin_RecruiterRepositoryReturnsNoItems() {
        when(mockRecruiterRepository.searchLicenseApprovalForAdmin("businessApprovalStatus",
                "additionalApprovalStatus")).thenReturn(Collections.emptyList());
        final List<Recruiter> result = recruiterServiceImplUnderTest.searchLicenseApprovalForAdmin(
                "businessApprovalStatus", "additionalApprovalStatus");
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testApproveLicense() {
        final ApprovalLicenseRequest request = new ApprovalLicenseRequest();
        request.setUserId(1L);
        request.setType("type");
        request.setApprovalStatus("approvalStatus");
        final Recruiter recruiter = recruiter();
        when(mockRecruiterRepository.getRecruiterByUserId(1L)).thenReturn(recruiter);
        final Recruiter recruiter1 = recruiter();
        when(mockRecruiterRepository.save(any(Recruiter.class))).thenReturn(recruiter1);
        final Recruiter result = recruiterServiceImplUnderTest.approveLicense(request);
        verify(mockRecruiterRepository).save(any(Recruiter.class));
    }

    @Test
    public void testApproveLicense_RecruiterRepositoryGetRecruiterByUserIdReturnsNull() {
        final ApprovalLicenseRequest request = new ApprovalLicenseRequest();
        request.setUserId(1L);
        request.setType("type");
        request.setApprovalStatus("approvalStatus");
        when(mockRecruiterRepository.getRecruiterByUserId(1L)).thenReturn(null);
        assertThatThrownBy(() -> recruiterServiceImplUnderTest.approveLicense(request))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testGetById() {
        final Recruiter recruiter = recruiter();
        when(mockRecruiterRepository.getById(1L)).thenReturn(recruiter);
        final Recruiter result = recruiterServiceImplUnderTest.getById(1L);
    }

    @Test
    public void testUpdateTotalCvView() {
        recruiterServiceImplUnderTest.updateTotalCvView(1L, 1L);
        verify(mockRecruiterRepository).updateTotalCvView(1L, 1L);
    }

    @Test
    public void testInsertGoogleRecruiter() {
        final GooglePojo googlePojo = new GooglePojo();
        googlePojo.setEmail("email");
        googlePojo.setName("fullName");
        googlePojo.setPicture("avatarUrl");

        final Users user = users();
        final Recruiter recruiter = recruiter();
        when(mockRecruiterRepository.save(any(Recruiter.class))).thenReturn(recruiter);
        recruiterServiceImplUnderTest.insertGoogleRecruiter(googlePojo, user);
        verify(mockRecruiterRepository).save(any(Recruiter.class));
    }

    @Test
    public void testGetTotalViewCV() {
        when(mockRecruiterRepository.getTotalViewCV(1L)).thenReturn(0);
        final Integer result = recruiterServiceImplUnderTest.getTotalViewCV(1L);
        assertThat(result).isEqualTo(0);
    }

    @Test
    public void testRemoveRecruiterFromCompany() {
        final Recruiter recruiter = recruiter();
        when(mockRecruiterRepository.getById(1L)).thenReturn(recruiter);
        final Recruiter recruiter1 = recruiter();
        when(mockRecruiterRepository.saveAndFlush(any(Recruiter.class))).thenReturn(recruiter1);
        recruiterServiceImplUnderTest.removeRecruiterFromCompany(1L);
        verify(mockRecruiterRepository).saveAndFlush(any(Recruiter.class));
    }
}
