package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CVService {
    List<CV> findCvByCandidateId(Long candidateId);

    CV getCVByCandidateId(long candidateId);

    Optional<CV> findCvById(long id);

    void updateSummary(long cvId, String newSummary);

    void updateUpdatedDateOfCV(long id, LocalDateTime updatedDate);

    Optional<CV> findByIdAndCandidateId(long id, long candidateId);

    void save(CV cv);

    Page<CV> findCVFilter(Pageable pageable, String experience, String candidateAddress, String techStack);

    int countCVInSystem();
}
