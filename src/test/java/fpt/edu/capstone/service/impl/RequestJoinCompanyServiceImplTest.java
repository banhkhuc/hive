package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.recruiter.ReceiveRequestJoinCompanyResponse;
import fpt.edu.capstone.entity.Company;
import fpt.edu.capstone.entity.RequestJoinCompany;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.RequestJoinCompanyRepository;
import fpt.edu.capstone.service.CompanyService;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestJoinCompanyServiceImplTest {

    @Mock
    private RequestJoinCompanyRepository mockRequestJoinCompanyRepository;
    @Mock
    private CompanyService mockCompanyService;

    private RequestJoinCompanyServiceImpl requestJoinCompanyServiceImplUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        requestJoinCompanyServiceImplUnderTest = new RequestJoinCompanyServiceImpl(mockRequestJoinCompanyRepository,
                mockCompanyService);
    }

    @Test
    void testCreateRequest() {
        final RequestJoinCompany requestJoinCompany = new RequestJoinCompany();
        requestJoinCompany.setId(1L);
        requestJoinCompany.setStatus("Pending");
        requestJoinCompany.setSenderId(1L);
        requestJoinCompany.setCompanyId(1L);
        requestJoinCompany.setApproverId(1L);

        final Company company1 = new Company();
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
        final Optional<Company> company = Optional.of(company1);
        when(mockCompanyService.findById(1L)).thenReturn(company);

        final RequestJoinCompany requestJoinCompany1 = new RequestJoinCompany();
        requestJoinCompany1.setId(1L);
        requestJoinCompany1.setStatus("Pending");
        requestJoinCompany1.setSenderId(1L);
        requestJoinCompany1.setCompanyId(1L);
        requestJoinCompany1.setApproverId(1L);
        when(mockRequestJoinCompanyRepository.save(any(RequestJoinCompany.class))).thenReturn(requestJoinCompany1);
        final RequestJoinCompany result = requestJoinCompanyServiceImplUnderTest.createRequest(requestJoinCompany);
    }

    @Test
    void testCreateRequest_CompanyServiceReturnsAbsent() {
        final RequestJoinCompany requestJoinCompany = new RequestJoinCompany();
        requestJoinCompany.setId(1L);
        requestJoinCompany.setStatus("Pending");
        requestJoinCompany.setSenderId(1L);
        requestJoinCompany.setCompanyId(1L);
        requestJoinCompany.setApproverId(1L);
        when(mockCompanyService.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> requestJoinCompanyServiceImplUnderTest.createRequest(requestJoinCompany))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    void testGetSentRequest() {
        final RequestJoinCompany requestJoinCompany1 = new RequestJoinCompany();
        requestJoinCompany1.setId(1L);
        requestJoinCompany1.setStatus("Pending");
        requestJoinCompany1.setSenderId(1L);
        requestJoinCompany1.setCompanyId(1L);
        requestJoinCompany1.setApproverId(1L);
        final Optional<RequestJoinCompany> requestJoinCompany = Optional.of(requestJoinCompany1);
        when(mockRequestJoinCompanyRepository.findBySenderId(1L)).thenReturn(requestJoinCompany);
        final Optional<RequestJoinCompany> result = requestJoinCompanyServiceImplUnderTest.getSentRequest(1L);
    }

    @Test
    void testGetSentRequest_RequestJoinCompanyRepositoryReturnsAbsent() {
        when(mockRequestJoinCompanyRepository.findBySenderId(1L)).thenReturn(Optional.empty());
        final Optional<RequestJoinCompany> result = requestJoinCompanyServiceImplUnderTest.getSentRequest(1L);
        assertThat(result).isEmpty();
    }

    @Test
    void testGetReceiveRequest() {
        final RequestJoinCompany requestJoinCompany = new RequestJoinCompany();
        requestJoinCompany.setId(1L);
        requestJoinCompany.setStatus("Pending");
        requestJoinCompany.setSenderId(1L);
        requestJoinCompany.setCompanyId(1L);
        requestJoinCompany.setApproverId(1L);
        final Page<RequestJoinCompany> requestJoinCompanies = new PageImpl<>(Arrays.asList(requestJoinCompany));
        when(mockRequestJoinCompanyRepository.findByApproverId(any(Pageable.class), eq(1L)))
                .thenReturn(requestJoinCompanies);
        final ResponseDataPagination result = requestJoinCompanyServiceImplUnderTest.getReceiveRequest(1, 10, 1L);
    }

    @Test
    void testGetReceiveRequest_RequestJoinCompanyRepositoryReturnsNoItems() {
        when(mockRequestJoinCompanyRepository.findByApproverId(any(Pageable.class), eq(1L)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final ResponseDataPagination result = requestJoinCompanyServiceImplUnderTest.getReceiveRequest(1, 10, 1L);
    }

    @Test
    void testApproveRequest() {
        requestJoinCompanyServiceImplUnderTest.approveRequest("status", 1L);
        verify(mockRequestJoinCompanyRepository).approveRequest("status", 1L);
    }

    @Test
    void testFindById() throws Exception {
        final RequestJoinCompany requestJoinCompany1 = new RequestJoinCompany();
        requestJoinCompany1.setId(1L);
        requestJoinCompany1.setStatus("Pending");
        requestJoinCompany1.setSenderId(1L);
        requestJoinCompany1.setCompanyId(1L);
        requestJoinCompany1.setApproverId(1L);
        final Optional<RequestJoinCompany> requestJoinCompany = Optional.of(requestJoinCompany1);
        when(mockRequestJoinCompanyRepository.findById(1L)).thenReturn(requestJoinCompany);
        final Optional<RequestJoinCompany> result = requestJoinCompanyServiceImplUnderTest.findById(1L);
    }

    @Test
    void testFindById_RequestJoinCompanyRepositoryReturnsAbsent() {
        when(mockRequestJoinCompanyRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<RequestJoinCompany> result = requestJoinCompanyServiceImplUnderTest.findById(1L);
        assertThat(result).isEmpty();
    }

//    @Test
//    void testGetReceiveRequestJoinCompanyWithFilter() {
//        when(mockRequestJoinCompanyRepository.getReceiveRequestJoinCompanyWithFilter(eq("fullName"), eq("email"),
//                eq("phone"), eq("status"), any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList()));
//        final Page<ReceiveRequestJoinCompanyResponse> result = requestJoinCompanyServiceImplUnderTest.getReceiveRequestJoinCompanyWithFilter(
//                "fullName", "email", "phone", "status", 10, 1);
//    }
//
//    @Test
//    void testGetReceiveRequestJoinCompanyWithFilter_RequestJoinCompanyRepositoryReturnsNoItems() {
//        when(mockRequestJoinCompanyRepository.getReceiveRequestJoinCompanyWithFilter(eq("fullName"), eq("email"),
//                eq("phone"), eq("status"), any(Pageable.class)))
//                .thenReturn(new PageImpl<>(Collections.emptyList()));
//        final Page<ReceiveRequestJoinCompanyResponse> result = requestJoinCompanyServiceImplUnderTest.getReceiveRequestJoinCompanyWithFilter(
//                "fullName", "email", "phone", "status", 10, 1);
//    }

    @Test
    void testDeleteOldSentRequest() {
        requestJoinCompanyServiceImplUnderTest.deleteOldSentRequest(1L);
        verify(mockRequestJoinCompanyRepository).deleteById(1L);
    }
}
