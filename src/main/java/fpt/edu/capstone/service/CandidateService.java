package fpt.edu.capstone.service;

import fpt.edu.capstone.common.user.GooglePojo;
import fpt.edu.capstone.dto.admin.user.CandidateManageResponse;
import fpt.edu.capstone.dto.candidate.CVBaseInformationRequest;
import fpt.edu.capstone.entity.Candidate;
import fpt.edu.capstone.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CandidateService  {
    List<Candidate> getAllCandidate() throws Exception;

    void insertCandidate( long userId);

    void updateCandidate(Candidate candidate, long id);

    Optional<Candidate> findById(long id);

    boolean existsById(long id);

    Candidate getCandidateById(long id);

    Optional<Candidate> findCandidateByUserId(long userId);

    void updateCandidateInformation(Candidate updatedCandidate);

    void updateCVInformation(CVBaseInformationRequest cvBaseInformationRequest);

    void updateIsNeedJob(boolean isNeedJob, long id);

    Page<CandidateManageResponse> searchCandidatesForAdmin(Pageable pageable, String username,
                                                           String email,String fullName, long userId, boolean isLocked);

    void save(Candidate candidate);

    void insertGoogleCandidate(GooglePojo googlePojo, Users user);

    void insertCandidateForRegister( long userId, String fullName);

}
