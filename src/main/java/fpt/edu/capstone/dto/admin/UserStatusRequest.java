package fpt.edu.capstone.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusRequest {
    private long userId;
    private boolean status;
}
