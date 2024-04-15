package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Payment;
import fpt.edu.capstone.entity.ProfileViewer;
import fpt.edu.capstone.repository.ProfileViewerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfileViewerServiceImplTest {

    @Mock
    private ProfileViewerRepository mockProfileViewerRepository;

    private ProfileViewerServiceImpl profileViewerServiceImplUnderTest;

    @Before
    public void setUp() {
        profileViewerServiceImplUnderTest = new ProfileViewerServiceImpl(mockProfileViewerRepository);
    }
    private ProfileViewer profileViewer(){
        ProfileViewer viewer = new ProfileViewer();
        viewer.setId(1L);
        viewer.setCvId(1L);
        viewer.setViewerId(1L);
        viewer.setCandidateId(1L);
        return viewer;
    }
    @Test
    public void testGetProfileViewerOfCv() {
        final ProfileViewer viewer = profileViewer();
        final Page<ProfileViewer> viewerPage = new PageImpl<>(Arrays.asList(viewer));
        when(mockProfileViewerRepository.getAllByCvId(any(Pageable.class), eq(1L))).thenReturn(viewerPage);
        final Page<ProfileViewer> result = profileViewerServiceImplUnderTest.getProfileViewerOfCv(PageRequest.of(1, 10),
                1L);
    }

    @Test
    public void testGetProfileViewerOfCv_ProfileViewerRepositoryReturnsNoItems() {
        when(mockProfileViewerRepository.getAllByCvId(any(Pageable.class), eq(1L)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<ProfileViewer> result = profileViewerServiceImplUnderTest.getProfileViewerOfCv(PageRequest.of(1, 10),
                1L);
    }

    @Test
    public void testGetByCvIdAndViewerId() {
        final ProfileViewer viewer = profileViewer();
        when(mockProfileViewerRepository.getByCvIdAndViewerId(1L, 1L)).thenReturn(viewer);
        final ProfileViewer result = profileViewerServiceImplUnderTest.getByCvIdAndViewerId(1L, 1L);
    }

    @Test
    public void testFindAll() {
        final ProfileViewer viewer = profileViewer();
        final Page<ProfileViewer> viewerPage = new PageImpl<>(Arrays.asList(viewer));
        when(mockProfileViewerRepository.findAll(PageRequest.of(1, 10))).thenReturn(viewerPage);
        final List<ProfileViewer> result = profileViewerServiceImplUnderTest.findAll(PageRequest.of(1, 10));
    }

    @Test
    public void testFindAll_ProfileViewerRepositoryReturnsNoItems() {
        when(mockProfileViewerRepository.findAll(PageRequest.of(1, 10)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final List<ProfileViewer> result = profileViewerServiceImplUnderTest.findAll(PageRequest.of(1, 10));
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testGetByCvIdAndViewerIdOptional() {
        final ProfileViewer viewer = profileViewer();
        final Optional<ProfileViewer> optional = Optional.of(viewer);
        when(mockProfileViewerRepository.getByCvIdAndViewerIdOptional(1L, 1L)).thenReturn(optional);
        final Optional<ProfileViewer> result = profileViewerServiceImplUnderTest.getByCvIdAndViewerIdOptional(1L, 1L);
    }

    @Test
    public void testGetByCvIdAndViewerIdOptional_ProfileViewerRepositoryReturnsAbsent() {
        when(mockProfileViewerRepository.getByCvIdAndViewerIdOptional(1L, 1L)).thenReturn(Optional.empty());
        final Optional<ProfileViewer> result = profileViewerServiceImplUnderTest.getByCvIdAndViewerIdOptional(1L, 1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testUpdateIsSave() {
        profileViewerServiceImplUnderTest.updateIsSave(false, 1L, 1L);
        verify(mockProfileViewerRepository).updateIsSave(false, 1L, 1L);
    }
}
