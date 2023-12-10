package fpt.edu.capstone.dto.CV;

import fpt.edu.capstone.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CVResponse {
    private long id;
    private long candidateId;
    private long isDeleted;
    private String summary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Certificate> certificates;
    private List<Education> educations;
    private List<Language> languages;
    private List<MajorLevel> majorLevels;
    private List<OtherSkill> otherSkills;
    private List<WorkExperience> workExperiences;

    public static CVResponse fromEntity(CV cv,
                                        List<Certificate> certificates,
                                        List<Education> educations,
                                        List<Language> languages,
                                        List<MajorLevel> majorLevels,
                                        List<OtherSkill> otherSkills,
                                        List<WorkExperience> workExperiences) {
        if (cv == null) {
            return null;
        }
        return CVResponse.builder().id(cv.getId())
                .candidateId(cv.getCandidateId())
                .isDeleted(cv.getIsDeleted())
                .summary(cv.getSummary())
                .createdAt(cv.getCreatedAt())
                .updatedAt(cv.getUpdatedAt())
                .certificates(certificates)
                .educations(educations)
                .languages(languages)
                .majorLevels(majorLevels)
                .otherSkills(otherSkills)
                .workExperiences(workExperiences).build();
    }

}
