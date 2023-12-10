package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.common.user.GooglePojo;
import fpt.edu.capstone.dto.admin.user.CandidateManageResponse;
import fpt.edu.capstone.dto.candidate.CVBaseInformationRequest;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.entity.Candidate;
import fpt.edu.capstone.entity.Users;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.CandidateRepository;
import fpt.edu.capstone.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Service
public class CandidateServiceImpl implements CandidateService {
    @Autowired
    CandidateRepository candidateRepository;

    @Override
    public List<Candidate> getAllCandidate() throws Exception {
        return candidateRepository.findAll();
    }

    @Override
    public void insertCandidate(long userId) {
        candidateRepository.insertCandidate(userId);
    }

    @Override
    public void updateCandidate(Candidate newCandidate, long id) {

    }

    @Override
    public Optional<Candidate> findById(long id) {
        return candidateRepository.findById(id);
    }

    @Override
    public boolean existsById(long id) {
        return candidateRepository.existsById(id);
    }

    @Override
    public Candidate getCandidateById(long id) {
        return candidateRepository.getCandidateById(id);
    }

    @Override
    public Optional<Candidate> findCandidateByUserId(long userId) {
        return candidateRepository.findCandidateByUserId(userId);
    }

    @Override
    public void updateCandidateInformation(Candidate updatedCandidate) {
        candidateRepository.updateCandidateInformation(updatedCandidate.isGender(), updatedCandidate.getBirthDate(), updatedCandidate.getCountry(), updatedCandidate.getFullName(), updatedCandidate.getAddress(), updatedCandidate.getSocialLink(), updatedCandidate.getIntroduction(), updatedCandidate.getId());
    }

    @Override
    public void updateCVInformation(CVBaseInformationRequest cvBaseInformationRequest) {
        candidateRepository.updateCVInformation(cvBaseInformationRequest.isGender(), cvBaseInformationRequest.getBirthDate(), cvBaseInformationRequest.getCountry(), cvBaseInformationRequest.getName(), cvBaseInformationRequest.getAddress(), cvBaseInformationRequest.getSocialLink(), cvBaseInformationRequest.getUserId());
    }

    @Override
    public void updateIsNeedJob(boolean isNeedJob, long id) {
        candidateRepository.updateIsNeedJob(isNeedJob, id);
    }

    @Override
    public Page<CandidateManageResponse> searchCandidatesForAdmin(Pageable pageable, String username, String email,
                                                                  String fullName, long userId, boolean isLocked) {
        return candidateRepository.searchCandidateForAdmin(pageable, username, email, fullName, userId, isLocked);
    }

    @Override
    public void save(Candidate candidate) {
        candidateRepository.save(candidate);
    }

    @Override
    public void insertGoogleCandidate(GooglePojo googlePojo, Users user) {
        Candidate candidate = new Candidate();
        candidate.setFullName(googlePojo.getName());
        candidate.setAvatarUrl(googlePojo.getPicture());
        candidate.setUserId(user.getId());
        candidateRepository.save(candidate);
        if(!candidateRepository.findById(candidate.getId()).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.CREATE_FAIL);
        }
    }

    @Override
    public void insertCandidateForRegister(long userId, String fullName) {
        Candidate candidate = new Candidate();
        candidate.setUserId(userId);
        candidate.setFullName(fullName);
        candidate.setExperienceLevel(0);
        candidateRepository.save(candidate);
    }
}
