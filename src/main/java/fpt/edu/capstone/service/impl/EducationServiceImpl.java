package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Education;
import fpt.edu.capstone.repository.EducationRepository;
import fpt.edu.capstone.service.EducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EducationServiceImpl implements EducationService {

    @Autowired
    EducationRepository educationRepository;

    @Override
    public List<Education> getListEducationByCvId(long cvId) {
        return educationRepository.getListEducationByCvId(cvId);
    }

    @Override

    public Education insertEducation(Education newEducation) {
        return educationRepository.save(newEducation);
    }

    @Override
    public void updateEducation(Education updateEducation) {
        educationRepository.updateEducation(updateEducation.getSchool(), updateEducation.getMajor(),
                updateEducation.getStartDate(), updateEducation.getEndDate(), updateEducation.getDescription(),
                updateEducation.isStudying(), updateEducation.getId());
    }

    @Override
    public Optional<Education> getEducationById(long educationId) {
        return educationRepository.findById(educationId);
    }

    @Override
    public void deleteEducation(long id) {
        educationRepository.deleteEducation(id);
    }
}
