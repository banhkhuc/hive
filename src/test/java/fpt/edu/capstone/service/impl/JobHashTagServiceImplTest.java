package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.JobHashtag;
import fpt.edu.capstone.repository.JobHashTagRepository;
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
public class JobHashTagServiceImplTest {

    @Mock
    private JobHashTagRepository mockJobHashTagRepository;

    private JobHashTagServiceImpl jobHashTagServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        jobHashTagServiceImplUnderTest = new JobHashTagServiceImpl(mockJobHashTagRepository);
    }
    private JobHashtag jobHashtag = new JobHashtag(1L, 1L, 1L, "hashTagName", "status");

    @Test
    public void testGetHashTagOfJob() {
        final List<JobHashtag> hashtagList = Arrays.asList(jobHashtag);
        when(mockJobHashTagRepository.getJobHashTagByJobId(1L)).thenReturn(hashtagList);
        final List<JobHashtag> result = jobHashTagServiceImplUnderTest.getHashTagOfJob(1L);
    }

    @Test
    public void testGetHashTagOfJob_JobHashTagRepositoryReturnsNoItems() {
        when(mockJobHashTagRepository.getJobHashTagByJobId(1L)).thenReturn(Collections.emptyList());
        final List<JobHashtag> result = jobHashTagServiceImplUnderTest.getHashTagOfJob(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }
}
