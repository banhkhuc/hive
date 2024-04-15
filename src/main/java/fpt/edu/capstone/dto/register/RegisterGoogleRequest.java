package fpt.edu.capstone.dto.register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterGoogleRequest {
    private String email;
    private long roleId;
}
