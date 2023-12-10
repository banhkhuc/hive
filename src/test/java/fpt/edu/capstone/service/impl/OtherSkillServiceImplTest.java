package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.OtherSkill;
import fpt.edu.capstone.repository.OtherSkillRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OtherSkillServiceImplTest {

    private OtherSkillServiceImpl otherSkillServiceImplUnderTest;

    private OtherSkill otherSkill = new OtherSkill(1L, "skillName", 1L, "level");
    @Before
    public void setUp() throws Exception {
        otherSkillServiceImplUnderTest = new OtherSkillServiceImpl();
        otherSkillServiceImplUnderTest.otherSkillRepository = mock(OtherSkillRepository.class);
    }

    @Test
    public void testGetListOtherSkillByCvId() {
        final List<OtherSkill> otherSkills = Arrays.asList(otherSkill);
        when(otherSkillServiceImplUnderTest.otherSkillRepository.getListOtherSkillByCvId(1L)).thenReturn(otherSkills);
        final List<OtherSkill> result = otherSkillServiceImplUnderTest.getListOtherSkillByCvId(1L);
    }

    @Test
    public void testGetListOtherSkillByCvId_OtherSkillRepositoryReturnsNoItems() {
        when(otherSkillServiceImplUnderTest.otherSkillRepository.getListOtherSkillByCvId(1L))
                .thenReturn(Collections.emptyList());
        final List<OtherSkill> result = otherSkillServiceImplUnderTest.getListOtherSkillByCvId(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testInsertOtherSkill() {
        final OtherSkill newOtherSkill = otherSkill;
        final OtherSkill otherSkill = new OtherSkill(1L, "skillName", 1L, "level");
        when(otherSkillServiceImplUnderTest.otherSkillRepository.save(any(OtherSkill.class))).thenReturn(otherSkill);
        final OtherSkill result = otherSkillServiceImplUnderTest.insertOtherSkill(newOtherSkill);
    }

    @Test
    public void testUpdateOtherSKill() {
        final OtherSkill otherSkill = new OtherSkill(1L, "skillName", 1L, "level");
        otherSkillServiceImplUnderTest.updateOtherSKill(otherSkill);
        verify(otherSkillServiceImplUnderTest.otherSkillRepository).updateOtherSkill("skillName", "level", 1L);
    }

    @Test
    public void testDeleteOtherSkill() {
        final OtherSkill otherSkill = new OtherSkill(1L, "skillName", 1L, "level");
        otherSkillServiceImplUnderTest.deleteOtherSkill(otherSkill);
        verify(otherSkillServiceImplUnderTest.otherSkillRepository).deleteOtherSkill(1L);
    }

    @Test
    public void testGetOtherSkillById() {
        final Optional<OtherSkill> otherSkill = Optional.of(new OtherSkill(1L, "skillName", 1L, "level"));
        when(otherSkillServiceImplUnderTest.otherSkillRepository.findById(1L)).thenReturn(otherSkill);
        final Optional<OtherSkill> result = otherSkillServiceImplUnderTest.getOtherSkillById(1L);
    }

    @Test
    public void testGetOtherSkillById_OtherSkillRepositoryReturnsAbsent() {
        when(otherSkillServiceImplUnderTest.otherSkillRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<OtherSkill> result = otherSkillServiceImplUnderTest.getOtherSkillById(1L);
        assertThat(result).isEmpty();
    }
}
