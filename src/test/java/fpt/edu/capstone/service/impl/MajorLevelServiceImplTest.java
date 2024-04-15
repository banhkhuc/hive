package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.MajorLevel;
import fpt.edu.capstone.repository.MajorLevelRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MajorLevelServiceImplTest {

    private MajorLevelServiceImpl majorLevelServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        majorLevelServiceImplUnderTest = new MajorLevelServiceImpl();
        majorLevelServiceImplUnderTest.majorLevelRepository = mock(MajorLevelRepository.class);
    }

    private MajorLevel majorLevel = new MajorLevel(1L, 1L, 1L, 1L, "level", false);
    @Test
    public void testGetListMajorLevelByCvId() {
        final List<MajorLevel> majorLevels = Arrays.asList(majorLevel);
        when(majorLevelServiceImplUnderTest.majorLevelRepository.getListMajorLevelByCvId(1L)).thenReturn(majorLevels);
        final List<MajorLevel> result = majorLevelServiceImplUnderTest.getListMajorLevelByCvId(1L);
    }

    @Test
    public void testGetListMajorLevelByCvId_MajorLevelRepositoryReturnsNoItems() {
        when(majorLevelServiceImplUnderTest.majorLevelRepository.getListMajorLevelByCvId(1L))
                .thenReturn(Collections.emptyList());
        final List<MajorLevel> result = majorLevelServiceImplUnderTest.getListMajorLevelByCvId(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testInsertNewMajorLevel() {
        final MajorLevel majorLevela = majorLevel;
        final MajorLevel majorLevel1a = majorLevel;
        when(majorLevelServiceImplUnderTest.majorLevelRepository.save(any(MajorLevel.class))).thenReturn(majorLevel1a);
        final MajorLevel result = majorLevelServiceImplUnderTest.insertNewMajorLevel(majorLevela);
    }

    @Test
    public void testDeleteMajorLevel() {
        final MajorLevel majorLevela = majorLevel;
        majorLevelServiceImplUnderTest.deleteMajorLevel(majorLevela);
        verify(majorLevelServiceImplUnderTest.majorLevelRepository).delete(any(MajorLevel.class));
    }

    @Test
    public void testUpdateMajorLevel() {
        final MajorLevel majorLevela = majorLevel;
        majorLevelServiceImplUnderTest.updateMajorLevel(majorLevela);
        verify(majorLevelServiceImplUnderTest.majorLevelRepository).updateNewMajorLevel(1L, 1L, "level", false, 1L);
    }

    @Test
    public void testGetMajorLevelById() {
        final Optional<MajorLevel> optional = Optional.of(majorLevel);
        when(majorLevelServiceImplUnderTest.majorLevelRepository.findById(1L)).thenReturn(optional);
        final Optional<MajorLevel> result = majorLevelServiceImplUnderTest.getMajorLevelById(1L);
    }

    @Test
    public void testGetMajorLevelById_MajorLevelRepositoryReturnsAbsent() {
        when(majorLevelServiceImplUnderTest.majorLevelRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<MajorLevel> result = majorLevelServiceImplUnderTest.getMajorLevelById(1L);
        assertThat(result).isEmpty();
    }
}
