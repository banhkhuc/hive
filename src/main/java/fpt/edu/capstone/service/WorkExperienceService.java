package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.WorkExperience;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkExperienceService {
    List<WorkExperience> getListWorkExperienceByCvId(Long cvId);

    WorkExperience insertWorkExperience(WorkExperience newWorkExperience);

    void updateWordExperience(WorkExperience workExperience);

    void deleteWordExperience(WorkExperience workExperience);

    Optional<WorkExperience> getWorkExperienceById(long id);
}
