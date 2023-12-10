package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Certificate;
import fpt.edu.capstone.repository.CertificateRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CertificateServiceImplTest {

    private CertificateServiceImpl certificateServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        certificateServiceImplUnderTest = new CertificateServiceImpl();
        certificateServiceImplUnderTest.certificateRepository = mock(CertificateRepository.class);
    }
    
    private Certificate certificate(){
        Certificate certificate = new Certificate(1L, "certificateName", "certificateUrl", 1L, 1L);;
        return certificate;
    }

    @Test
    public void testGetListCertificateByCvId() {
        final List<Certificate> certificates = Arrays.asList(certificate());
        when(certificateServiceImplUnderTest.certificateRepository.getListCertificateByCvId(1L))
                .thenReturn(certificates);
        final List<Certificate> result = certificateServiceImplUnderTest.getListCertificateByCvId(1L);
    }

    @Test
    public void testGetListCertificateByCvId_CertificateRepositoryReturnsNoItems() {
        when(certificateServiceImplUnderTest.certificateRepository.getListCertificateByCvId(1L))
                .thenReturn(Collections.emptyList());
        final List<Certificate> result = certificateServiceImplUnderTest.getListCertificateByCvId(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testInsertCertificate() {
        final Certificate newCertificate = certificate();
        final Certificate certificate = certificate();
        when(certificateServiceImplUnderTest.certificateRepository.save(any(Certificate.class)))
                .thenReturn(certificate);
        final Certificate result = certificateServiceImplUnderTest.insertCertificate(newCertificate);
    }

    @Test
    public void testUpdateService() {
        final Certificate certificate = certificate();
        certificateServiceImplUnderTest.updateService(certificate);
        verify(certificateServiceImplUnderTest.certificateRepository).updateCertificate("certificateName",
                "certificateUrl", 1L, 1L);
    }

    @Test
    public void testDeleteCertificate() {
        final Certificate certificate = certificate();
        certificateServiceImplUnderTest.deleteCertificate(certificate);
        verify(certificateServiceImplUnderTest.certificateRepository).deleteCertificate(1L);
    }

    @Test
    public void testGetCertificateById() {
        final Optional<Certificate> certificate = Optional.of(certificate());
        when(certificateServiceImplUnderTest.certificateRepository.findById(1L)).thenReturn(certificate);
        final Optional<Certificate> result = certificateServiceImplUnderTest.getCertificateById(1L);
    }

    @Test
    public void testGetCertificateById_CertificateRepositoryReturnsAbsent() {
        when(certificateServiceImplUnderTest.certificateRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<Certificate> result = certificateServiceImplUnderTest.getCertificateById(1L);
        assertThat(result).isEmpty();
    }
}
