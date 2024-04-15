package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.CVImported;
import fpt.edu.capstone.repository.CVImportedRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CVImportedServiceTest {

    @Mock
    private CVImportedRepository mockCvImportedRepository;

    @InjectMocks
    private CVImportedService cvImportedServiceUnderTest;

    private CVImported cvImported(){
        CVImported cvImported =new CVImported();
        cvImported.setId("id");
        cvImported.setName("name");
        cvImported.setContentType("contentType");
        cvImported.setSize(1L);
        cvImported.setCandidateId(1L);
        cvImported.setCreateAt(LocalDateTime.now());
        cvImported.setData("content".getBytes());
        return cvImported;
    }
    @Test
    public void testSave() throws Exception {
        final MultipartFile file = new MockMultipartFile("sourceFile.tmp", "Hello World".getBytes());
        final CVImported cvImported = cvImported();
        when(mockCvImportedRepository.save(any(CVImported.class))).thenReturn(cvImported);
        final CVImported result = cvImportedServiceUnderTest.save(file, "JPG", 1L);
    }

    @Test
    public void testSave_ThrowsIOException() throws Exception{
        final MultipartFile file = null;
        final CVImported cvImported = cvImported();
        when(mockCvImportedRepository.save(any(CVImported.class))).thenReturn(cvImported);
        assertThatThrownBy(() -> cvImportedServiceUnderTest.save(file, "IMG", 1L)).isInstanceOf(IOException.class);
    }

    @Test
    public void testFindById() {
        final CVImported cvImported1 = cvImported();
        final Optional<CVImported> cvImported = Optional.of(cvImported1);
        when(mockCvImportedRepository.findById("id")).thenReturn(cvImported);
        final Optional<CVImported> result = cvImportedServiceUnderTest.findById("id");
    }

    @Test
    public void testFindById_CVImportedRepositoryReturnsAbsent() {
        when(mockCvImportedRepository.findById("id")).thenReturn(Optional.empty());
        final Optional<CVImported> result = cvImportedServiceUnderTest.findById("id");
        assertThat(result).isEmpty();
    }
}
