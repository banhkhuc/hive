package fpt.edu.capstone.dto.recruiter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterBaseOnCompanyResponse {

    private long recruiterId;
    private String userName;
    private String avatar;
    private String fullName;
    private boolean gender;
    private String position;
    private String linkedinAccount;
    private String email;
    private String phone;

}
