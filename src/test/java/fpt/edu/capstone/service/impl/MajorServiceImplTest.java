package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Major;
import fpt.edu.capstone.repository.MajorRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MajorServiceImplTest {

    @Mock
    private MajorRepository mockMajorRepository;

    private MajorServiceImpl majorServiceImplUnderTest;

    private Major major = new Major(1L, 1L, "majorName", "status");
    @Before
    public void setUp() throws Exception {
        majorServiceImplUnderTest = new MajorServiceImpl(mockMajorRepository);
    }

    @Test
    public void testGetNameByMajorId() {
        when(mockMajorRepository.getNameByMajorId(1L)).thenReturn("result");
        final String result = majorServiceImplUnderTest.getNameByMajorId(1L);
        assertThat(result).isEqualTo("result");
    }

    @Test
    public void testGetAllMajorByFieldId() {
        final List<Major> majors = Arrays.asList(major);
        when(mockMajorRepository.getAllMajorByFieldId(1L)).thenReturn(majors);
        final List<Major> result = majorServiceImplUnderTest.getAllMajorByFieldId(1L);
    }

    @Test
    public void testGetAllMajorByFieldId_MajorRepositoryReturnsNoItems() {
        when(mockMajorRepository.getAllMajorByFieldId(1L)).thenReturn(Collections.emptyList());
        final List<Major> result = majorServiceImplUnderTest.getAllMajorByFieldId(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testGetMajorNameByCVId() {
        when(mockMajorRepository.getMajorNameByCVId(1L)).thenReturn(Arrays.asList("value"));
        final List<String> result = majorServiceImplUnderTest.getMajorNameByCVId(1L);
        assertThat(result).isEqualTo(Arrays.asList("value"));
    }

    @Test
    public void testGetMajorNameByCVId_MajorRepositoryReturnsNoItems() {
        when(mockMajorRepository.getMajorNameByCVId(1L)).thenReturn(Collections.emptyList());
        final List<String> result = majorServiceImplUnderTest.getMajorNameByCVId(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }
}
