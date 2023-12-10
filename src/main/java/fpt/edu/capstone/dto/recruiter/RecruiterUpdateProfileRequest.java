package fpt.edu.capstone.dto.recruiter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecruiterUpdateProfileRequest {
    private String fullName;
    private String phone;
    private boolean gender;
    private String position;
    private String linkedinAccount;
    private String avatarUrl;
}
