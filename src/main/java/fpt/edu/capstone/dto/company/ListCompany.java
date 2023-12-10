package fpt.edu.capstone.dto.company;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListCompany {
    private long id;
    private String fieldWork;
    private String name;
    private String avatar;
    private String email;
    private String phone;
    private String description;
    private String website;
    private String numberEmployees;
    private String address;
    private String taxCode;
    private String mapUrl;
    private long creatorId;
    private boolean isLocked;
}
