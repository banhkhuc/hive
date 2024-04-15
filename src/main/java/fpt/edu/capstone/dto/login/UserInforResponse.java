package fpt.edu.capstone.dto.login;

import fpt.edu.capstone.entity.Admin;
import fpt.edu.capstone.entity.Candidate;
import fpt.edu.capstone.entity.Recruiter;
import fpt.edu.capstone.entity.Users;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInforResponse {
    private Users user;
    private Candidate candidate;
    private Recruiter recruiter;
    private Admin admin;
    private boolean joinedCompany;
    private boolean approvedBusinessLicense;
    private boolean isVerifiedEmail;
    private boolean createdOrRequestedJoinCompany;
}
