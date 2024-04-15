package fpt.edu.capstone.dto.company;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopCompanyResponse {
    private long applyCvNumber;
    private long companyId;
    private String companyName;
    private String companyAvatar;
}
