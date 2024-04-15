package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.JobHashtag;

import java.util.List;

public interface JobHashTagService {
    List<JobHashtag> getHashTagOfJob(long jobId);
}
