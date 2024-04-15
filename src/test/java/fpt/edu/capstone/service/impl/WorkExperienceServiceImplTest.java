package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.WorkExperience;
import fpt.edu.capstone.repository.WorkExperienceRepository;
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

public class WorkExperienceServiceImplTest {

    private WorkExperienceServiceImpl workExperienceServiceImplUnderTest;

    @Before
    public void setUp() {
        workExperienceServiceImplUnderTest = new WorkExperienceServiceImpl();
        workExperienceServiceImplUnderTest.workExperienceRepository = mock(WorkExperienceRepository.class);
    }

    private WorkExperience workExperience(){
        WorkExperience workExperience = new WorkExperience();
        workExperience.setId(1L);
        workExperience.setCvId(1L);
        workExperience.setCompanyName("companyName");
        workExperience.setPosition("position");
        workExperience.setStartDate(LocalDateTime.of(2022,8,16,16,16,16));
        workExperience.setEndDate(LocalDateTime.of(2022,8,16,16,16,16));
        workExperience.setDescription("description");
        workExperience.setWorking(false);
        return workExperience;
    }
    @Test
    public void testGetListWorkExperienceByCvId() {
        final List<WorkExperience> workExperiences = Arrays.asList(workExperience());
        when(workExperienceServiceImplUnderTest.workExperienceRepository.getListWorkExperienceByCvId(1L))
                .thenReturn(workExperiences);
        final List<WorkExperience> result = workExperienceServiceImplUnderTest.getListWorkExperienceByCvId(1L);
    }

    @Test
    public void testGetListWorkExperienceByCvId_WorkExperienceRepositoryReturnsNoItems() {
        when(workExperienceServiceImplUnderTest.workExperienceRepository.getListWorkExperienceByCvId(1L))
                .thenReturn(Collections.emptyList());
        final List<WorkExperience> result = workExperienceServiceImplUnderTest.getListWorkExperienceByCvId(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testInsertWorkExperience() {
        final WorkExperience newWorkExperience = workExperience();
        final WorkExperience workExperience = workExperience();
        when(workExperienceServiceImplUnderTest.workExperienceRepository.save(any(WorkExperience.class)))
                .thenReturn(workExperience);
        final WorkExperience result = workExperienceServiceImplUnderTest.insertWorkExperience(newWorkExperience);
    }

    @Test
    public void testUpdateWordExperience() {
        final WorkExperience workExperience = workExperience();
        workExperienceServiceImplUnderTest.updateWordExperience(workExperience);
        verify(workExperienceServiceImplUnderTest.workExperienceRepository).updateWordExperience("companyName",
                "position", LocalDateTime.of(2022,8,16,16,16,16), LocalDateTime.of(2022,8,16,16,16,16), "description", 1L);
    }

    @Test
    public void testDeleteWordExperience() {
        final WorkExperience workExperience =workExperience();
        workExperienceServiceImplUnderTest.deleteWordExperience(workExperience);
        verify(workExperienceServiceImplUnderTest.workExperienceRepository).delete(any(WorkExperience.class));
    }

    @Test
    public void testGetWorkExperienceById() {
        final Optional<WorkExperience> optional = Optional.of(workExperience());
        when(workExperienceServiceImplUnderTest.workExperienceRepository.findById(1L)).thenReturn(optional);
        final Optional<WorkExperience> result = workExperienceServiceImplUnderTest.getWorkExperienceById(1L);
    }

    @Test
    public void testGetWorkExperienceById_WorkExperienceRepositoryReturnsAbsent() {
        when(workExperienceServiceImplUnderTest.workExperienceRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<WorkExperience> result = workExperienceServiceImplUnderTest.getWorkExperienceById(1L);
        assertThat(result).isEmpty();
    }
}
