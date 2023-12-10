package fpt.edu.capstone.repository;

import com.amazonaws.services.apigateway.model.Op;
import fpt.edu.capstone.dto.company.CompanyResponse;
import fpt.edu.capstone.dto.recruiter.CountCandidateApplyPercentageResponse;
import fpt.edu.capstone.dto.recruiter.CountTotalCreatedJobResponse;
import fpt.edu.capstone.entity.AppliedJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppliedJobRepository extends JpaRepository<AppliedJob, Long> {

    boolean existsByCandidateIdAndJobId(long candidateId, long jobId);

    @Query(value = "select * from applied_job aj where aj.candidate_id = ?1 and job_id = ?2 " +
            "order by created_at desc limit 1", nativeQuery = true)
    AppliedJob getByCandidateIdAndJobId(long candidateId, long jobId);

    @Query(value = "select * from applied_job aj where aj.job_id = ?1 and aj.is_applied = ?2", nativeQuery = true)
    Page<AppliedJob> getCvAppliedJob(Pageable pageable, long jobId, boolean isApplied);

    @Query(value = "select * from applied_job aj where job_id = ?1 and candidate_id = ?2 " +
            "and approval_status like concat('%', ?3, '%') and is_applied = ?4 " +
            "order by created_at desc limit 1;", nativeQuery = true)
    AppliedJob getAppliedJobPendingApproval(long jobId, long candidateId, String approvalStatus, boolean isApplied);

    @Query(value = "select count(job_id) as applyCvNumber, t1.company_id as companyId, t1.company_name as companyName from " +
            "(select aj.job_id, j.company_id, c.name as company_name from applied_job aj " +
            "join job j on aj.job_id = j.id " +
            "join companies c on j.company_id = c.id where aj.created_at > current_date - interval '365' day) t1 " +
            "group by company_id, t1.company_name order by applyCvNumber desc limit 12;", nativeQuery = true)
    List<CompanyResponse> getTopCompaniesHomepage();

    @Query(value = "select * from applied_job aj where candidate_id = :candidateId " +
            "and (approval_status like (:status) or :status is null or :status = '') and is_applied = true",
            nativeQuery = true)
    Page<AppliedJob> searchAppliedJobsOfCandidate(Pageable pageable, @Param("candidateId") long candidateId,@Param("status") String approvalStatus);

    @Query(value = "select count(aj.candidate_id) as totalApplied, aj.job_id as jobId, j.number_recruits as numberRecruits " +
            "from applied_job aj join job j on aj.job_id = j.id where j.recruiter_id = ? group by job_id , number_recruits", nativeQuery = true)
    List<CountCandidateApplyPercentageResponse> countApplyPercentage(long recruiterId);

    @Query(value = "select count(*) from applied_job aj where job_id = ? and is_applied = true", nativeQuery = true)
    int countAppliedCVOfAJob(long jobId);

    @Query(value = "select count(*) from applied_job aj where is_applied = true;", nativeQuery = true)
    int countAppliedCVInSystem();

    @Query(value = "select * from applied_job aj where job_id = ?2 and candidate_id = ?1 and is_applied = true", nativeQuery = true)
    Optional<AppliedJob> getAppliedJobByJobIDandCandidateID(long jobId, long candidateId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE applied_job set is_seen_uploaded_cv = true where job_id = ?1 and candidate_id = ?2 and is_applied = true", nativeQuery = true)
    void updateIsSeenUploadedCV(long jobId, long candidateId);

}
