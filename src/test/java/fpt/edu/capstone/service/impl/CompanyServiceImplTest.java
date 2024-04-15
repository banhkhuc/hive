package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.company.ListCompany;
import fpt.edu.capstone.entity.Company;
import fpt.edu.capstone.repository.CompanyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompanyServiceImplTest {

    @Mock
    private CompanyRepository mockCompanyRepository;
    @Mock
    private ImageServiceImpl imageService;
    @Mock
    private ModelMapper modelMapper;
    private CompanyServiceImpl companyServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        companyServiceImplUnderTest = new CompanyServiceImpl(mockCompanyRepository, imageService, modelMapper);
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
    @Test
    public void testGetCompanyById() {
        final Company company = company();
        when(mockCompanyRepository.getCompanyById(1L)).thenReturn(company);
        final Company result = companyServiceImplUnderTest.getCompanyById(1L);
    }

    @Test
    public void testExistById() {
        when(mockCompanyRepository.existsById(1L)).thenReturn(false);
        final boolean result = companyServiceImplUnderTest.existById(1L);
        assertThat(result).isFalse();
    }

    @Test
    public void testGetAllCompany() {
        final Company company = company();
        final List<Company> companies = Arrays.asList(company);
        when(mockCompanyRepository.findAll()).thenReturn(companies);
        final List<ListCompany> result = companyServiceImplUnderTest.getAllCompany();
    }

    @Test
    public void testGetAllCompany_CompanyRepositoryReturnsNoItems() {
        when(mockCompanyRepository.findAll()).thenReturn(Collections.emptyList());
        final List<ListCompany> result = companyServiceImplUnderTest.getAllCompany();
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testFindById() {
        final Company company1 = company();
        final Optional<Company> company = Optional.of(company1);
        when(mockCompanyRepository.findById(1L)).thenReturn(company);
        final Optional<Company> result = companyServiceImplUnderTest.findById(1L);
    }

    @Test
    public void testFindById_CompanyRepositoryReturnsAbsent() {
        when(mockCompanyRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<Company> result = companyServiceImplUnderTest.findById(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testSearchCompany() {
        final Company company = company();
        final Page<Company> companies = new PageImpl<>(Arrays.asList(company));
        when(mockCompanyRepository.getAllByName(any(Pageable.class), eq("companyName"))).thenReturn(companies);
        final Page<Company> result = companyServiceImplUnderTest.searchCompany(PageRequest.of(1, 10), "companyName");
    }

    @Test
    public void testSearchCompany_CompanyRepositoryReturnsNoItems() {
        when(mockCompanyRepository.getAllByName(any(Pageable.class), eq("companyName")))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<Company> result = companyServiceImplUnderTest.searchCompany(PageRequest.of(1, 10), "companyName");
    }

    @Test
    public void testGetAdditionCompanies() {
        final Company company = company();
        final List<Company> companies = Arrays.asList(company);
        when(mockCompanyRepository.getAdditionCompanies(0, Arrays.asList(1L))).thenReturn(companies);
        final List<Company> result = companyServiceImplUnderTest.getAdditionCompanies(0, Arrays.asList(1L));
    }

}
