package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.Education;

import java.util.List;
import java.util.Optional;

public interface EducationService {
    List<Education> getListEducationByCvId(long cvId);

    Education insertEducation(Education newEducation);

    void updateEducation(Education updateEducation);

    Optional<Education> getEducationById(long educationId);

    void deleteEducation(long id);
}
