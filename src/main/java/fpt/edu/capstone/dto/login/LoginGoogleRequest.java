package fpt.edu.capstone.dto.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginGoogleRequest {
    //FE trả
    private String name;
    private String email;
    private String picture;
    private long roleId;
}
