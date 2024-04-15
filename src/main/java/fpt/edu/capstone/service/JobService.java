package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.job.CreateJobRequest;
import fpt.edu.capstone.dto.job.HomePageData;
import fpt.edu.capstone.dto.job.JobResponse;
import fpt.edu.capstone.dto.job.UpdateJobRequest;
import fpt.edu.capstone.dto.recruiter.CountTotalCreatedJobResponse;
import fpt.edu.capstone.entity.Job;
import fpt.edu.capstone.utils.Pagination;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface JobService {
    void createJob(CreateJobRequest request);

    ResponseDataPagination searchListJobFilter(Integer pageNo,Integer pageSize, long fieldId, long countryId,
                                               String jobName, String workForm, String workPlace, long fromSalary, long toSalary);

    void updateJob(UpdateJobRequest request);

    void saveJob(Job job);

    void deleteJob(long jobId);

    boolean existsById(long id);

    Page<Job> getNewestJobList(Pageable pageable);

    ResponseDataPagination getJobByFieldId(Integer pageNo, Integer pageSize, long id);

    List<JobResponse> getListSuggestJobByCv(long candidateId);

    Page<Job> getUrgentJobList(Pageable pageable);

    Page<Job> getPopularJobList(Pageable pageable, List<Long> popularJobId);

    Job getJobById(long jobId);

    Page<Job> getJobOfRecruiter(Pageable pageable, long recruiterId, String jobName);

    Optional<Job> findById(long id);

    Page<Job> getJobByCompanyId(long pageNo, long quantity, long companyId);

    CountTotalCreatedJobResponse countTotalCreatedJobOfRecruiter(long recruiterId);

    List<Job> getJobByRecruiterId(long recId);

    List<JobResponse> getSameJobsOtherCompanies(long detailJobId);

    HomePageData getDataHomePage();

    int countJobInSystem();

    void draftJob(long jobId);

    List <Long> getListPopularJobId();

    List <Job> findAll();
}
