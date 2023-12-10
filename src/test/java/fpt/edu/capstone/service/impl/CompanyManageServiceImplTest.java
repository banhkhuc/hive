package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.company.CreateCompanyRequest;
import fpt.edu.capstone.dto.company.TopCompanyResponse;
import fpt.edu.capstone.dto.company.UpdateCompanyInforRequest;
import fpt.edu.capstone.entity.Company;
import fpt.edu.capstone.entity.Fields;
import fpt.edu.capstone.entity.Image;
import fpt.edu.capstone.entity.Recruiter;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.CompanyRepository;
import fpt.edu.capstone.repository.ImageRepository;
import fpt.edu.capstone.service.AppliedJobService;
import fpt.edu.capstone.service.CompanyService;
import fpt.edu.capstone.service.FieldsService;
import fpt.edu.capstone.service.RecruiterService;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
public class CompanyManageServiceImplTest {

    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private AppliedJobService mockAppliedJobService;
    @Mock
    private ImageServiceImpl mockImageService;
    @Mock
    private RecruiterService mockRecruiterService;
    @Mock
    private CompanyService mockCompanyService;
    @Mock
    private CompanyRepository mockCompanyRepository;
    @Mock
    private FieldsService mockFieldsService;
    @Mock
    private ImageRepository mockImageRepository;

    private CompanyManageServiceImpl companyManageServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        companyManageServiceImplUnderTest = new CompanyManageServiceImpl(mockModelMapper, mockAppliedJobService,
                mockImageService, mockRecruiterService, mockCompanyService, mockCompanyRepository, mockFieldsService,
                mockImageRepository);
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

    private UpdateCompanyInforRequest request(){
        UpdateCompanyInforRequest request = new UpdateCompanyInforRequest();
        request.setCompanyId(1L);
        request.setCompanyEmail("companyEmail");
        request.setCompanyPhone("companyPhone");
        request.setCompanyDescription("companyDescription");
        request.setCompanyWebsite("companyWebsite");
        request.setNumberEmployees("numberEmployees");
        request.setCompanyAddress("address");
        request.setTaxCode("taxCode");
        request.setMapUrl("mapUrl");
        request.setAvatarUrl("avatarUrl");
        request.setCoverImageUrl("coverImageUrl");
        request.setUploadImageUrlList(Arrays.asList("value"));
        return request;
    }

    CreateCompanyRequest companyRequest (){
        CreateCompanyRequest request = new CreateCompanyRequest();
        request.setFieldWork("Công nghệ thông tin");
        request.setName("nameName");
        request.setEmail("email");
        request.setPhoneNumber("companyPhone");
        request.setDescription("description");
        request.setWebsite("website");
        request.setNumberEmployees("numberEmployees");
        request.setAddress("address");
        request.setAvatarName("avatarName");
        request.setUrl("url");
        request.setTaxCode("6541326541");
        request.setMapUrl("mapUrl");
        request.setCreatorId(1L);
        return request;
    }

    private Image image(){
        Image image =  new Image(1L, "name", "url", 1L,
                0, false, "contentType", ("" +
                "content").getBytes(), false);
        return image;
    }
    @Test
    public void testGetTop12Companies() {
        when(mockAppliedJobService.getTopCompaniesHomepage()).thenReturn(Arrays.asList());
        final Optional<Image> image = Optional.of(image());
        when(mockImageService.findAvatarByCompanyId(1L)).thenReturn(image);
        final List<TopCompanyResponse> result = companyManageServiceImplUnderTest.getTopCompaniesHomepage();
    }

    @Test
    public void testGetTop12Companies_AppliedJobServiceReturnsNoItems() {
        when(mockAppliedJobService.getTopCompaniesHomepage()).thenReturn(Collections.emptyList());
        final Optional<Image> image = Optional.of(image());
        when(mockImageService.findAvatarByCompanyId(1L)).thenReturn(image);
        final List<TopCompanyResponse> result = companyManageServiceImplUnderTest.getTopCompaniesHomepage();
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testGetTop12Companies_ImageServiceImplReturnsAbsent() {
        when(mockAppliedJobService.getTopCompaniesHomepage()).thenReturn(Arrays.asList());
        when(mockImageService.findAvatarByCompanyId(1L)).thenReturn(Optional.empty());
        final List<TopCompanyResponse> result = companyManageServiceImplUnderTest.getTopCompaniesHomepage();
    }

    @Test
    public void testUpdateCompanyInformation() {
        final UpdateCompanyInforRequest request = request();
        request.setDeleteImageIdList(Arrays.asList(1L));
        final Optional<Recruiter> recruiter = Optional.of(recruiter());
        when(mockRecruiterService.findById(1L)).thenReturn(recruiter);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final UpdateCompanyInforRequest result = companyManageServiceImplUnderTest.updateCompanyInformation(1L,
                request);
        verify(mockImageService).saveImageCompany(true, false, 1L, Arrays.asList("avatarUrl"));
        verify(mockImageService).deleteImageById(Arrays.asList(1L));
    }

    @Test
    public void testUpdateCompanyInformation_RecruiterServiceReturnsAbsent() {
        final UpdateCompanyInforRequest request = request();
        when(mockRecruiterService.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> companyManageServiceImplUnderTest.updateCompanyInformation(1L, request))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testUpdateCompanyInformation_CompanyServiceReturnsNull() {
        final UpdateCompanyInforRequest request = request();
        request.setDeleteImageIdList(Arrays.asList(1L));
        final Optional<Recruiter> recruiter = Optional.of(recruiter());
        when(mockRecruiterService.findById(1L)).thenReturn(recruiter);
        when(mockCompanyService.getCompanyById(1L)).thenReturn(null);
        assertThatThrownBy(() -> companyManageServiceImplUnderTest.updateCompanyInformation(1L, request))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testSearchCompany() {
        final Company company = company();
        final Page<Company> companies = new PageImpl<>(Arrays.asList(company));
        when(mockCompanyService.searchCompany(any(Pageable.class), eq("companyName"))).thenReturn(companies);
        final ResponseDataPagination result = companyManageServiceImplUnderTest.searchCompany(1, 10, "companyName");
    }

    @Test
    public void testSearchCompany_CompanyServiceReturnsNoItems() {
        when(mockCompanyService.searchCompany(any(Pageable.class), eq("companyName")))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final ResponseDataPagination result = companyManageServiceImplUnderTest.searchCompany(1, 10, "companyName");
    }

    @Test
    public void testLockCompany() {
        final Company company1 = company();
        final Optional<Company> company = Optional.of(company1);
        when(mockCompanyService.findById(1L)).thenReturn(company);
        final Company company2 = company();
        when(mockCompanyRepository.save(any(Company.class))).thenReturn(company2);
        companyManageServiceImplUnderTest.lockCompany(1L);
        verify(mockCompanyRepository).save(any(Company.class));
    }

    @Test
    public void testLockCompany_CompanyServiceReturnsAbsent() {
        when(mockCompanyService.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> companyManageServiceImplUnderTest.lockCompany(1L))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testCreateCompany() {
        final CreateCompanyRequest request = companyRequest();
        final Company company1 = company();
        final Optional<Company> company = Optional.of(company1);
        when(mockCompanyRepository.getCompanyByName("name")).thenReturn(company);
        final Optional<Fields> fields = Optional.of(new Fields(1L, "fieldName", "status"));
        when(mockFieldsService.findById(1L)).thenReturn(fields);
        final Company company3 = company();
        final Optional<Company> company2 = Optional.of(company3);
        when(mockCompanyRepository.getCompanyByTaxcode("taxCode")).thenReturn(company2);
        final Company company4 = company();
        when(mockModelMapper.map(any(Object.class), eq(Company.class))).thenReturn(company4);
        final Company company5 = company();
        when(mockCompanyRepository.save(any(Company.class))).thenReturn(company5);
        final Image image = image();
        when(mockImageRepository.save(any(Image.class))).thenReturn(image);
        final Company company6 = company();
        when(mockCompanyRepository.getCompanyById(1L)).thenReturn(company6);

        final Company result = companyManageServiceImplUnderTest.createCompany(request);
        verify(mockCompanyRepository).save(any(Company.class));
        verify(mockImageRepository).save(any(Image.class));
    }

    @Test
    public void testCreateCompany_CompanyRepositoryGetCompanyByNameReturnsAbsent() {
        final CreateCompanyRequest request = companyRequest();
        when(mockCompanyRepository.getCompanyByName("name")).thenReturn(Optional.empty());
        final Optional<Fields> fields = Optional.of(new Fields(1L, "fieldName", "status"));
        when(mockFieldsService.findById(1L)).thenReturn(fields);
        final Company company1 = company();
        final Optional<Company> company = Optional.of(company1);
        when(mockCompanyRepository.getCompanyByTaxcode("taxCode")).thenReturn(company);
        final Company company2 = company();
        when(mockModelMapper.map(any(Object.class), eq(Company.class))).thenReturn(company2);
        final Company company3 = company();
        when(mockCompanyRepository.save(any(Company.class))).thenReturn(company3);
        final Image image = image();
        when(mockImageRepository.save(any(Image.class))).thenReturn(image);
        final Company company4 = company();
        when(mockCompanyRepository.getCompanyById(1L)).thenReturn(company4);
        final Company result = companyManageServiceImplUnderTest.createCompany(request);
        verify(mockCompanyRepository).save(any(Company.class));
        verify(mockImageRepository).save(any(Image.class));
    }

    @Test
    public void testCreateCompany_FieldsServiceReturnsAbsent() {
        final CreateCompanyRequest request = companyRequest();
        final Company company1 = company();
        final Optional<Company> company = Optional.of(company1);
        when(mockCompanyRepository.getCompanyByName("name")).thenReturn(company);
        when(mockFieldsService.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> companyManageServiceImplUnderTest.createCompany(request))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testCreateCompany_CompanyRepositoryGetCompanyByTaxcodeReturnsAbsent() {
        final CreateCompanyRequest request = companyRequest();
        final Company company1 = company();
        final Optional<Company> company = Optional.of(company1);
        when(mockCompanyRepository.getCompanyByName("name")).thenReturn(company);
        final Optional<Fields> fields = Optional.of(new Fields(1L, "fieldName", "status"));
        when(mockFieldsService.findById(1L)).thenReturn(fields);
        when(mockCompanyRepository.getCompanyByTaxcode("taxCode")).thenReturn(Optional.empty());
        final Company company2 = company();
        when(mockModelMapper.map(any(Object.class), eq(Company.class))).thenReturn(company2);
        final Company company3 = company();
        when(mockCompanyRepository.save(any(Company.class))).thenReturn(company3);
        final Image image = image();
        when(mockImageRepository.save(any(Image.class))).thenReturn(image);
        final Company company4 = company();
        when(mockCompanyRepository.getCompanyById(1L)).thenReturn(company4);
        final Company result = companyManageServiceImplUnderTest.createCompany(request);
        verify(mockCompanyRepository).save(any(Company.class));
        verify(mockImageRepository).save(any(Image.class));
    }
}
