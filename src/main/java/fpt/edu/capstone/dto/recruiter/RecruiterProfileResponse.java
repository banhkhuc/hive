package fpt.edu.capstone.dto.recruiter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
public class RecruiterProfileResponse {
    private long recruiterId;
    private String userName;
    private String avatarName;
    private String avatarUrl;
    private long companyId;
    private String companyName;
    private String companyAddress;
    private String email;
    private String fullName;
    private String phone;
    private boolean gender;
    private String position;
    private String linkedinAccount;
    private boolean joinedCompany;
    private boolean approvedBusinessLicense;
    private boolean isVerifiedPhone;
    private int totalCvView;
}
