package fpt.edu.capstone.dto.company;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCompanyRequest {
    //Lĩnh vực hoạt động
    private String fieldWork;
    private String name;
    private String email;
    private String phoneNumber;
    private String description;
    private String website;
    private String numberEmployees;
    private String address;
    private String avatarName;
    private String url;
    private String taxCode;
    private String mapUrl;
    private long creatorId;
}
