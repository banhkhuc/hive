package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.JobHashtag;
import fpt.edu.capstone.repository.JobHashTagRepository;
import fpt.edu.capstone.service.JobHashTagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class JobHashTagServiceImpl implements JobHashTagService {

    private final JobHashTagRepository jobHashTagRepository;

    @Override
    public List<JobHashtag> getHashTagOfJob(long jobId) {
        return jobHashTagRepository.getJobHashTagByJobId(jobId);
    }
}
