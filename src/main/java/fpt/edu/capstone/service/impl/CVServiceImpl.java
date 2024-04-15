package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.CV;
import fpt.edu.capstone.repository.CVRepository;
import fpt.edu.capstone.service.CVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CVServiceImpl implements CVService {

    @Autowired
    CVRepository cvRepository;

    public List<CV> findCvByCandidateId(Long candidateId) {
        return cvRepository.findCvByCandidateId(candidateId);
    }

    @Override
    public Optional<CV> findCvById(long id) {
        return cvRepository.findById(id);
    }

    @Override
    public void updateSummary(long cvId, String newSummary) {
        cvRepository.updateSummary(cvId, newSummary);
    }

    @Override
    public void updateUpdatedDateOfCV(long id, LocalDateTime updatedDate) {
        cvRepository.updateUpdatedDateOfCV(id,updatedDate);
    }

    @Override
    public Optional<CV> findByIdAndCandidateId(long id, long candidateId) {
        return cvRepository.findByIdAndCAndCandidateId(id, candidateId);
    }

    @Override
    public void save(CV cv) {
        cvRepository.save(cv);
    }

    @Override
    public Page<CV> findCVFilter(Pageable pageable, String experience, String candidateAddress, String techStack) {
        return cvRepository.findCVFilter(pageable, experience, candidateAddress, techStack);
    }

    @Override
    public int countCVInSystem() {
        return cvRepository.countCV();
    }

    public CV getCVByCandidateId(long candidateId) { //Nam
        return cvRepository.getByCandidateId(candidateId);
    }
}
