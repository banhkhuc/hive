package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Education;
import fpt.edu.capstone.repository.EducationRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EducationServiceImplTest {

    private EducationServiceImpl educationServiceImplUnderTest;

    private Education education = new Education(1L, 1L, "school", "major", LocalDateTime.of(2020,1,1,1,1,1),
            LocalDateTime.of(2020,1,1,1,1,1), false, "description");
    @Before
    public void setUp() throws Exception {
        educationServiceImplUnderTest = new EducationServiceImpl();
        educationServiceImplUnderTest.educationRepository = mock(EducationRepository.class);
    }

    @Test
    public void testGetListEducationByCvId() {
        final List<Education> educationList = Arrays.asList(education);
        when(educationServiceImplUnderTest.educationRepository.getListEducationByCvId(1L)).thenReturn(educationList);
        final List<Education> result = educationServiceImplUnderTest.getListEducationByCvId(1L);
    }

    @Test
    public void testGetListEducationByCvId_EducationRepositoryReturnsNoItems() {
        when(educationServiceImplUnderTest.educationRepository.getListEducationByCvId(1L))
                .thenReturn(Collections.emptyList());
        final List<Education> result = educationServiceImplUnderTest.getListEducationByCvId(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testInsertEducation() {
        final Education newEducation = education;
        final Education educations = education;
        when(educationServiceImplUnderTest.educationRepository.save(any(Education.class))).thenReturn(educations);
        final Education result = educationServiceImplUnderTest.insertEducation(newEducation);
    }

    @Test
    public void testUpdateEducation() {
        final Education updateEducation = education;
        educationServiceImplUnderTest.updateEducation(updateEducation);
        verify(educationServiceImplUnderTest.educationRepository).updateEducation("school", "major",
                LocalDateTime.of(2020,1,1,1,1,1),
                LocalDateTime.of(2020,1,1,1,1,1),
                "description", false, 1L);
    }

    @Test
    public void testGetEducationById() {
        final Optional<Education> optional = Optional.of(education);
        when(educationServiceImplUnderTest.educationRepository.findById(1L)).thenReturn(optional);
        final Optional<Education> result = educationServiceImplUnderTest.getEducationById(1L);
    }

    @Test
    public void testGetEducationById_EducationRepositoryReturnsAbsent() {
        when(educationServiceImplUnderTest.educationRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<Education> result = educationServiceImplUnderTest.getEducationById(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testDeleteEducation() {
        educationServiceImplUnderTest.deleteEducation(1L);
        verify(educationServiceImplUnderTest.educationRepository).deleteEducation(1L);
    }
}
