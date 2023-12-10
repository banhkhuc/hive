package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.job.ApprovalJobRequest;
import fpt.edu.capstone.dto.job.JobDetailResponse;
import fpt.edu.capstone.utils.ResponseDataPagination;

public interface CandidateJobService {
    ResponseDataPagination getNewestJob(Integer pageNo, Integer pageSize);

    ResponseDataPagination getUrgentJob(Integer pageNo, Integer pageSize);

    ResponseDataPagination getPopularJob(Integer pageNo, Integer pageSize);

    void approveJob(ApprovalJobRequest request);

    JobDetailResponse getJobDetail(long jobId, long candidateId);

    ResponseDataPagination getListJobByWorkForm(Integer pageNo, Integer pageSize, String workForm);

}
