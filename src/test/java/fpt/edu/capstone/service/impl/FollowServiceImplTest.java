package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.job.JobResponse;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.repository.FollowRepository;
import fpt.edu.capstone.service.CompanyService;
import fpt.edu.capstone.service.ImageService;
import fpt.edu.capstone.service.JobHashTagService;
import fpt.edu.capstone.service.JobService;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FollowServiceImplTest {

    @Mock
    private FollowRepository mockFollowRepository;
    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private JobService mockJobService;
    @Mock
    private CompanyService mockCompanyService;
    @Mock
    private ImageService mockImageService;
    @Mock
    private JobHashTagService mockJobHashTagService;

    private FollowServiceImpl followServiceImplUnderTest;

    private Image imageEntity = new Image(1L, "name", "url", 1L,
            0, false, "contentType", "content".getBytes(), false);

    private Job job(){
        Job job = new Job();
        job.setId(1L);
        job.setCompanyId(1L);
        job.setJobName("jobName");
        job.setWorkPlace("workPlace");
        job.setWorkForm("workForm");
        job.setStartDate(LocalDateTime.now());
        job.setEndDate(LocalDateTime.now().plusDays(10));
        job.setFromSalary(1L);
        job.setToSalary(1L);
        job.setNumberRecruits(1L);
        job.setRank("rank");
        job.setExperience("experience");
        job.setGender(false);
        job.setJobDescription("jobDescription");
        job.setJobRequirement("jobRequirement");
        job.setBenefit("benefit");
        job.setFieldId(1L);
        job.setIsDeleted(0);
        job.setRecruiterId(1L);
        job.setWeekday("weekday");
        job.setCountryId(1L);
        job.setFlag("Posted");
        return job;
    }

    private Company company(){
        Company company1 = new Company();
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
        return company1;
    }
    
    private JobResponse jobResponse (){
        JobResponse jobResponse = new JobResponse();
        jobResponse.setJobId(1L);
        jobResponse.setCompanyId(1L);
        jobResponse.setRecruiterId(1L);
        jobResponse.setListHashtag(Arrays.asList("value"));
        jobResponse.setCompanyName("companyName");
        jobResponse.setJobName("jobName");
        jobResponse.setJobDescription("jobDescription");
        jobResponse.setJobRequirement("jobRequirement");
        jobResponse.setBenefit("benefit");
        jobResponse.setFromSalary(1L);
        jobResponse.setToSalary(1L);
        jobResponse.setNumberRecruits(1L);
        jobResponse.setRank("rank");
        jobResponse.setWorkForm("workForm");
        jobResponse.setCompanyAvatar("url");
        return jobResponse;
    }
    
    @Before
    public void setUp() throws Exception {
        followServiceImplUnderTest = new FollowServiceImpl(mockFollowRepository, mockModelMapper, mockJobService,
                mockCompanyService, mockImageService, mockJobHashTagService);
    }

    @Test
    public void testUnFollow() {
        followServiceImplUnderTest.unFollow(1L, 1L, 1L);
        verify(mockFollowRepository).unFollow(1L, 1L, 1L);
    }

    @Test
    public void testInsertFollow() {
        final Follow follow = new Follow(1L, 1L, 1L, 1L);
        when(mockFollowRepository.save(any(Follow.class))).thenReturn(new Follow(1L, 1L, 1L, 1L));
        final Follow result = followServiceImplUnderTest.insertFollow(follow);
    }

    @Test
    public void testIsFollowing() {
        when(mockFollowRepository.getFollowIfHave(1L, 1L, 1L)).thenReturn(Optional.of(new Follow(1L, 1L, 1L, 1L)));
        final Boolean result = followServiceImplUnderTest.isFollowing(1L, 1L, 1L);
        assertThat(result).isTrue();
    }

    @Test
    public void testIsFollowing_FollowRepositoryReturnsAbsent() {
        when(mockFollowRepository.getFollowIfHave(1L, 1L, 1L)).thenReturn(Optional.empty());
        final Boolean result = followServiceImplUnderTest.isFollowing(1L, 1L, 1L);
        assertThat(result).isFalse();
    }

    @Test
    public void testGetFollowedJobByCandidateID() {
        when(mockFollowRepository.getFollowedJobByCandidateID(any(Pageable.class), eq(1L)))
                .thenReturn(new PageImpl<>(Arrays.asList()));
        
        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);
        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Image image = imageEntity;
//        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = followServiceImplUnderTest.getFollowedJobByCandidateID(1, 11, 11L);
    }

    @Test
    public void testGetFollowedJobByCandidateID_FollowRepositoryReturnsNoItems() {
        when(mockFollowRepository.getFollowedJobByCandidateID(any(Pageable.class), eq(1L)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);
        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Image image = imageEntity;
//        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = followServiceImplUnderTest.getFollowedJobByCandidateID(1, 11, 11L);
    }

    @Test
    public void testGetFollowedJobByCandidateID_JobHashTagServiceReturnsNull() {
        when(mockFollowRepository.getFollowedJobByCandidateID(any(Pageable.class), eq(1L)))
                .thenReturn(new PageImpl<>(Arrays.asList()));
        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);
        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(null);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Image image = imageEntity;
//        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = followServiceImplUnderTest.getFollowedJobByCandidateID(1, 11, 11L);
    }

    @Test
    public void testGetFollowedJobByCandidateID_JobHashTagServiceReturnsNoItems() {
        when(mockFollowRepository.getFollowedJobByCandidateID(any(Pageable.class), eq(1L)))
                .thenReturn(new PageImpl<>(Arrays.asList()));
        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);
        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(Collections.emptyList());
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        final Image image = imageEntity;
//        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = followServiceImplUnderTest.getFollowedJobByCandidateID(1, 11, 11L);
    }

    @Test
    public void testGetFollowedJobByCandidateID_CompanyServiceReturnsNull() {
        when(mockFollowRepository.getFollowedJobByCandidateID(any(Pageable.class), eq(1L)))
                .thenReturn(new PageImpl<>(Arrays.asList()));
        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);
        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        when(mockCompanyService.getCompanyById(1L)).thenReturn(null);
        final Image image = imageEntity;
//        when(mockImageService.getImageCompany(1L, true)).thenReturn(image);
        final ResponseDataPagination result = followServiceImplUnderTest.getFollowedJobByCandidateID(1, 11, 11L);
    }

    @Test
    public void testGetFollowedJobByCandidateID_ImageServiceReturnsNull() {
        when(mockFollowRepository.getFollowedJobByCandidateID(any(Pageable.class), eq(1L)))
                .thenReturn(new PageImpl<>(Arrays.asList()));
        final Job job = job();
        when(mockJobService.getJobById(1L)).thenReturn(job);
        final JobResponse jobResponse = jobResponse();
        when(mockModelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
        final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
        when(mockJobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
        final Company company = company();
        when(mockCompanyService.getCompanyById(1L)).thenReturn(company);
        when(mockImageService.getImageCompany(1L, true)).thenReturn(null);
        final ResponseDataPagination result = followServiceImplUnderTest.getFollowedJobByCandidateID(1, 11, 11L);
    }

    @Test
    public void testGetAllFollowerOfAJob() {
        final Optional<List<Follow>> follows = Optional.of(Arrays.asList(new Follow(1L, 1L, 1L, 1L)));
        when(mockFollowRepository.getAllFollowerOfAJob(1L)).thenReturn(follows);
        final Optional<List<Follow>> result = followServiceImplUnderTest.getAllFollowerOfAJob(1L);
    }

    @Test
    public void testGetAllFollowerOfAJob_FollowRepositoryReturnsAbsent() {
        when(mockFollowRepository.getAllFollowerOfAJob(1L)).thenReturn(Optional.empty());
        final Optional<List<Follow>> result = followServiceImplUnderTest.getAllFollowerOfAJob(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testGetAllFollowerOfAJob_FollowRepositoryReturnsNoItems() {
        when(mockFollowRepository.getAllFollowerOfAJob(1L)).thenReturn(Optional.of(Collections.emptyList()));
        final Optional<List<Follow>> result = followServiceImplUnderTest.getAllFollowerOfAJob(1L);
        assertThat(result).isEqualTo(Optional.of(Collections.emptyList()));
    }
}
