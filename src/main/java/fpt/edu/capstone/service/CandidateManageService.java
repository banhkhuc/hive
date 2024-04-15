package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.CV.CVRequest;
import fpt.edu.capstone.dto.CV.CVResponse;
import fpt.edu.capstone.dto.job.AppliedJobRequest;
import fpt.edu.capstone.entity.CV;
import fpt.edu.capstone.utils.ResponseDataPagination;

import java.util.List;

public interface CandidateManageService {
    ResponseDataPagination searchAppliedJobsOfCandidate(Integer pageNo, Integer pageSize, long candidateId, String approvalStatus);

    ResponseDataPagination getJobsOfCompany(Integer pageNo, Integer pageSize, long companyId);

    CVResponse findCvByCandidateId(long candidateId);

    CV createCV(CVRequest request);

    ResponseDataPagination getProfileViewer(Integer pageNo, Integer pageSize, long cvId, long candidateId);

    String appliedJob(AppliedJobRequest request) throws Exception;

}
