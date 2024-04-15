package fpt.edu.capstone.dto.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
        private String oldPassword; //nhập mật khẩu cũ
        private String newPassword; // đổi mật khẩu mới
        private String confirmPassword; // nhập lại mật khẩu mới
}
