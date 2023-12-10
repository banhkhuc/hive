package fpt.edu.capstone.dto.candidate;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CandidateBaseInformationResponse {
    private long id;
    private long userId;
    private boolean gender;
    private LocalDateTime birthDate;
    private String country;
    private String fullName;
    private String address;
    private String socialLink;
    private String avatarUrl;
    private boolean isNeedJob;
    private long experienceLevel;
    private String introduction;
    private String phoneNumber;
    private boolean isVerifiedPhone;
}
