package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.company.CompanyResponse;
import fpt.edu.capstone.dto.recruiter.CountCandidateApplyPercentageResponse;
import fpt.edu.capstone.entity.AppliedJob;
import fpt.edu.capstone.repository.AppliedJobRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AppliedJobServiceImplTest {

    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private AppliedJobRepository mockAppliedJobRepository;

    private AppliedJobServiceImpl appliedJobServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        appliedJobServiceImplUnderTest = new AppliedJobServiceImpl(mockAppliedJobRepository);
    }

    @Test
    public void testGetAppliedJobPendingApproval() {
        final AppliedJob appliedJob = new AppliedJob(1L, 1L, 1L, false, "approvalStatus", false, "cvUploadUrl", false);
        when(mockAppliedJobRepository.getAppliedJobPendingApproval(1L, 1L, "status", false)).thenReturn(appliedJob);
        final AppliedJob result = appliedJobServiceImplUnderTest.getAppliedJobPendingApproval(1L, 1L);
    }

    @Test
    public void testGetAppliedJobBefore() {
        final AppliedJob appliedJob = new AppliedJob(1L, 1L, 1L, false, "approvalStatus", false, "cvUploadUrl", false);
        when(mockAppliedJobRepository.getByCandidateIdAndJobId(1L, 1L)).thenReturn(appliedJob);
        final AppliedJob result = appliedJobServiceImplUnderTest.getAppliedJobBefore(1L, 1L);
    }

    @Test
    public void testGetCvAppliedJob() {
        final Page<AppliedJob> appliedJobs = new PageImpl<>(
                Arrays.asList(new AppliedJob(1L, 1L, 1L, false, "approvalStatus", false, "cvUploadUrl", false)));
        when(mockAppliedJobRepository.getCvAppliedJob(any(Pageable.class), eq(1L), eq(false))).thenReturn(appliedJobs);
        final Page<AppliedJob> result = appliedJobServiceImplUnderTest.getCvAppliedJob(PageRequest.of(1, 10), 1L, false);
    }

    @Test
    public void testGetCvAppliedJob_AppliedJobRepositoryReturnsNoItems() {
        when(mockAppliedJobRepository.getCvAppliedJob(any(Pageable.class), eq(1L), eq(false)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<AppliedJob> result = appliedJobServiceImplUnderTest.getCvAppliedJob(PageRequest.of(1, 10), 1L, false);
    }

    @Test
    public void testGetTop12Companies() {
        when(mockAppliedJobRepository.getTopCompaniesHomepage()).thenReturn(Arrays.asList());
        final List<CompanyResponse> result = appliedJobServiceImplUnderTest.getTopCompaniesHomepage();
    }

    @Test
    public void testGetTop12Companies_AppliedJobRepositoryReturnsNoItems() {
        when(mockAppliedJobRepository.getTopCompaniesHomepage()).thenReturn(Collections.emptyList());
        final List<CompanyResponse> result = appliedJobServiceImplUnderTest.getTopCompaniesHomepage();
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testSearchAppliedJobsOfCandidate() {
        final Page<AppliedJob> appliedJobs = new PageImpl<>(
                Arrays.asList(new AppliedJob(1L, 1L, 1L, false, "approvalStatus", false, "cvUploadUrl", false)));
        when(mockAppliedJobRepository.searchAppliedJobsOfCandidate(any(Pageable.class), eq(1L),
                eq("approvalStatus"))).thenReturn(appliedJobs);
        final Page<AppliedJob> result = appliedJobServiceImplUnderTest.searchAppliedJobsOfCandidate(
                PageRequest.of(1, 10), 1L, "approvalStatus");
    }

    @Test
    public void testSearchAppliedJobsOfCandidate_AppliedJobRepositoryReturnsNoItems() {
        when(mockAppliedJobRepository.searchAppliedJobsOfCandidate(any(Pageable.class), eq(1L),
                eq("approvalStatus"))).thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<AppliedJob> result = appliedJobServiceImplUnderTest.searchAppliedJobsOfCandidate(
                PageRequest.of(1, 10), 1L, "approvalStatus");
    }

    @Test
    public void testCountApplyPercentage() {
        when(mockAppliedJobRepository.countApplyPercentage(1L)).thenReturn(Arrays.asList());
        final List<CountCandidateApplyPercentageResponse> result = appliedJobServiceImplUnderTest.countApplyPercentage(
                1L);
    }

    @Test
    public void testCountApplyPercentage_AppliedJobRepositoryReturnsNoItems() {
        when(mockAppliedJobRepository.countApplyPercentage(1L)).thenReturn(Collections.emptyList());
        final List<CountCandidateApplyPercentageResponse> result = appliedJobServiceImplUnderTest.countApplyPercentage(
                1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testCountAppliedCVOfJob() {
        when(mockAppliedJobRepository.countAppliedCVOfAJob(1L)).thenReturn(0);
        final int result = appliedJobServiceImplUnderTest.countAppliedCVOfJob(1L);
        assertThat(result).isEqualTo(0);
    }

    @Test
    public void testCountAppliedCVInSystem() {
        when(mockAppliedJobRepository.countAppliedCVInSystem()).thenReturn(0);
        final int result = appliedJobServiceImplUnderTest.countAppliedCVInSystem();
        assertThat(result).isEqualTo(0);
    }

    @Test
    public void testGetAppliedJobByJobIDandCandidateID() {
        final Optional<AppliedJob> appliedJob = Optional.of(new AppliedJob());
        when(mockAppliedJobRepository.getAppliedJobByJobIDandCandidateID(1L, 1L)).thenReturn(appliedJob);
        final Optional<AppliedJob> result = appliedJobServiceImplUnderTest.getAppliedJobByJobIDandCandidateID(1L, 1L);
    }

    @Test
    public void testUpdateIsSeenUploadedCV() {
        appliedJobServiceImplUnderTest.updateIsSeenUploadedCV(1L, 1L);
        verify(mockAppliedJobRepository).updateIsSeenUploadedCV(1L, 1L);
    }
}
