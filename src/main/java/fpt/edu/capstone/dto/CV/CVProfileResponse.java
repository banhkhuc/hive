package fpt.edu.capstone.dto.CV;

import fpt.edu.capstone.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CVProfileResponse {

    private long candidateId;
    private boolean gender;
    private LocalDateTime birthDate;
    private String country;
    private String fullName;
    private String address;
    private String socialLink;
    private String avatarUrl;

    private long experienceLevel;
    private String introduction;
    private String phoneNumber;
    private String email;


    private String summary;
    private List<Certificate> certificates;
    private List<Education> educations;
    private List<Language> languages;
    private List<MajorLevel> majorLevels;
    private String majorName;
    private List<OtherSkill> otherSkills;
    private List<WorkExperience> workExperiences;
}
