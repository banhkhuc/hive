package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.WorkExperience;
import fpt.edu.capstone.repository.WorkExperienceRepository;
import fpt.edu.capstone.service.WorkExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WorkExperienceServiceImpl implements WorkExperienceService {

    @Autowired
    WorkExperienceRepository workExperienceRepository;

    @Override
    public List<WorkExperience> getListWorkExperienceByCvId(Long cvId) {
        return workExperienceRepository.getListWorkExperienceByCvId(cvId);
    }

    @Override
    public WorkExperience insertWorkExperience(WorkExperience newWorkExperience) {
        return workExperienceRepository.save(newWorkExperience);
    }

    @Override
    public void updateWordExperience(WorkExperience workExperience) {
        workExperienceRepository.updateWordExperience(workExperience.getCompanyName(), workExperience.getPosition(),workExperience.getStartDate(), workExperience.getEndDate(), workExperience.getDescription(), workExperience.getId());
    }

    @Override
    public void deleteWordExperience(WorkExperience workExperience) {
        workExperienceRepository.delete(workExperience);
    }

    @Override
    public Optional<WorkExperience> getWorkExperienceById(long id) {
        return workExperienceRepository.findById(id);
    }
}
