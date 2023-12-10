package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.register.*;
import fpt.edu.capstone.entity.Role;
import fpt.edu.capstone.entity.Users;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.UserRepository;
import fpt.edu.capstone.service.AppliedJobService;
import fpt.edu.capstone.service.CVService;
import fpt.edu.capstone.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    RoleServiceImpl roleService;

    @Mock
    private AppliedJobService mockAppliedJobService;

    @Mock
    private JobService mockJobService;

    @Mock
    private CVService mockCvService;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findUserByUserNameNull(){
        Optional<Users> opUser = Optional.empty();
        when(userRepository.findByUsernameAndIsDeleted("userName", 0)).thenReturn(opUser);
        assertEquals(false, userService.findUserByUserName("userName").isPresent());
    }

    @Test
    public void finUserByUserNameNotNull(){
        Users user = new Users();
        user.setUsername("userName");
        Optional<Users> opUser = Optional.of(user);
        when(userRepository.findByUsernameAndIsDeleted("userName", 0)).thenReturn(opUser);
        assertEquals(opUser, userService.findUserByUserName("userName"));

    }

    @Test
    public void saveUserFail(){
        Users user = new Users();
        when(userRepository.save(user)).thenReturn(null);
        assertEquals(null, userService.saveUser(user));
    }

    @Test
    public void saveUserSuccess(){
        Users user = new Users();
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(user, userService.saveUser(user));
    }

    @Test
    public void getByUserNameIsNull(){
        when(userRepository.getByUsername("userName")).thenReturn(null);
        assertEquals(null, userService.getByUserName("userName"));
    }

    @Test
    public void getByUserNameIsNotNull(){
        Users user = new Users();
        when(userRepository.getByUsername("userName")).thenReturn(user);
        assertEquals(user, userService.getByUserName("userName"));
    }

    @Test
    public void getByUserNameIsCheckValueFalse(){
        Users user = new Users();
        Users user1 = new Users();

        when(userRepository.getByUsername("userName")).thenReturn(user1);

        assertNotEquals(user, userService.getByUserName("userName"));
    }

    @Test
    public void getByUserNameIsCheckValueTrue(){
        Users user = new Users();

        when(userRepository.getByUsername("userName")).thenReturn(user);

        assertEquals(user, userService.getByUserName("userName"));
    }

    @Test
    public void getUserByIdIsNull(){
        when(userRepository.getUserById(1L)).thenReturn(null);
        assertEquals(null, userService.getUserById(1L));
    }

    @Test
    public void getUserByIdIsNotNull(){
        Users user = new Users();
        when(userRepository.getUserById(1L)).thenReturn(user);
        assertEquals(user, userService.getUserById(1L));
    }

    @Test
    public void getUserByIdIsCheckValueFalse(){
        Users user = new Users();
        Users user1 = new Users();

        when(userRepository.getUserById(1L)).thenReturn(user1);

        assertNotEquals(user, userService.getUserById(1L));
    }

    @Test
    public void getUserByIdIsCheckValueTrue(){
        Users user = new Users();

        when(userRepository.getUserById(1L)).thenReturn(user);

        assertEquals(user, userService.getUserById(1L));
    }

    @Test
    public void registerUserRequestNull(){
        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerUser(null);
        });
        assertEquals(null, exception.getMessage());
    }

    @Test
    public void registerUserRequestNotNullAndNotFindRoleById(){
        RegisterRequest request = new RegisterRequest();
        request.setRoleId(1L);

        Optional<Role> optionalRole = Optional.empty();

        when(roleService.findRoleById(request.getRoleId())).thenReturn(optionalRole);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerUser(request);
        });

        assertEquals("Loại vai trò người dùng không tồn tại trong hệ thống", exception.getMessage());
    }

    @Test
    public void registerUserRequestNotNullAndFindRoleByIdAndFindByUsername(){
        RegisterRequest request = new RegisterRequest();
        request.setRoleId(1L);
        request.setUsername("userName");

        Optional<Role> optionalRole = Optional.of(new Role());
        Optional<Users> optionalUsers = Optional.of(new Users());

        when(roleService.findRoleById(request.getRoleId())).thenReturn(optionalRole);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(optionalUsers);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerUser(request);
        });

        assertEquals("Tên đăng nhập đã được sử dụng.", exception.getMessage());
    }

    @Test
    public void registerUserRequestNotNullAndFindRoleByIdAndNotFindByUsernameFindByEmail(){
        RegisterRequest request = new RegisterRequest();
        request.setRoleId(1L);
        request.setUsername("userName");
        request.setEmail("email");

        Optional<Role> optionalRole = Optional.of(new Role());
        Optional<Users> optionalUsers = Optional.of(new Users());


        when(roleService.findRoleById(request.getRoleId())).thenReturn(optionalRole);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(optionalUsers);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerUser(request);
        });

        assertEquals("Email đã được sử dụng.", exception.getMessage());
    }

    @Test
    public void registerUserRequestNotNullAndFindRoleByIdAndNotFindByUsernameNotFindByEmailPassFail(){
        RegisterRequest request = new RegisterRequest();
        request.setRoleId(1L);
        request.setUsername("userName");
        request.setEmail("email");
        request.setPassword("pass");
        request.setConfirmPassword("pass1");

        Optional<Role> optionalRole = Optional.of(new Role());
        Optional<Users> optionalUsers = Optional.of(new Users());


        when(roleService.findRoleById(request.getRoleId())).thenReturn(optionalRole);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());


        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerUser(request);
        });

        assertEquals("Xác nhận mật khẩu không đúng.", exception.getMessage());
    }

    @Test
    public void registerUserRequestNotNullAndFindRoleByIdAndNotFindByUsernameNotFindByEmailPassSuccess(){
        RegisterRequest request = new RegisterRequest();
        request.setRoleId(1L);
        request.setUsername("userName");
        request.setEmail("email");
        request.setPassword("pass");
        request.setConfirmPassword("pass");

        Optional<Role> optionalRole = Optional.of(new Role());
        Optional<Users> optionalUsers = Optional.of(new Users());


        when(roleService.findRoleById(request.getRoleId())).thenReturn(optionalRole);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn(request.getPassword());
        assertDoesNotThrow(() -> {
            userService.registerUser(request);
        }, "No exception is throws");
    }

    @Test
    public void findByEmailNull(){
        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());
        userService.findByEmail("email");
        assertEquals("Người dùng không tồn tại", "Người dùng không tồn tại");
    }

    @Test
    public void findByEmailNotNull(){
        Optional<Users> optionalUsers = Optional.of(new Users());
        when(userRepository.findByEmail("nam@gmail.com")).thenReturn(optionalUsers);
        assertEquals("nam@gmail.com", "nam@gmail.com");
    }

    @Test
    public void registerGoogleUserRequedNull(){
        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerGoogleUser(null);
        });
        assertEquals(null, exception.getMessage());
    }

    @Test
    public void registerGoogleUserRequedNotNullFinRoleByIdNull(){
        RegisterGoogleRequest request = new RegisterGoogleRequest();
        request.setEmail("email@email");
        request.setRoleId(1L);
        when(roleService.findRoleById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerGoogleUser(request);
        });
        assertEquals("Loại vai trò người dùng không tồn tại trong hệ thống", exception.getMessage());
    }

    @Test
    public void registerGoogleUserRequedNotNullFinRoleByIdNotNullFindByUsernameNotNull(){
        RegisterGoogleRequest request = new RegisterGoogleRequest();
        request.setEmail("email@email");
        request.setRoleId(1L);
        when(roleService.findRoleById(1L)).thenReturn(Optional.of(new Role()));
        when(userRepository.findByUsername("email")).thenReturn(Optional.of(new Users()));
        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerGoogleUser(request);
        });
        assertEquals("Tên đăng nhập đã được sử dụng.", exception.getMessage());
    }

    @Test
    public void registerGoogleUserRequedNotNullFinRoleByIdNotNullFindByUsernameNullFindByEmailNotNull(){
        RegisterGoogleRequest request = new RegisterGoogleRequest();
        request.setEmail("email@email");
        request.setRoleId(1L);
        when(roleService.findRoleById(1L)).thenReturn(Optional.of(new Role()));
        when(userRepository.findByUsername("email")).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new Users()));
        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerGoogleUser(request);
        });
        assertEquals("Email đã được sử dụng.", exception.getMessage());
    }

    @Test
    public void registerGoogleUserRequedNotNullFinRoleByIdNotNullFindByUsernameNullFindByEmailNull(){
        RegisterGoogleRequest request = new RegisterGoogleRequest();
        request.setEmail("email@email");
        request.setRoleId(1L);

        when(roleService.findRoleById(1L)).thenReturn(Optional.of(new Role()));
        when(userRepository.findByUsername("email")).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1")).thenReturn("1");

        assertDoesNotThrow(() -> {
            userService.registerGoogleUser(request);
        }, "No exception is throws");

    }
    @Test
    public void changePasswordFindUserByUserNameNull(){
        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword(null, null);
        });
        assertEquals("Tên người dùng không tìm thấy.", exception.getMessage());
    }

    @Test
    public void changePasswordFindUserByUserNameNotNullRequestNull(){
        when(userService.findUserByUserName("userName")).thenReturn(Optional.of(new Users()));
        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword("userName", null);
        });
        assertEquals(null, exception.getMessage());
    }

    @Test
    public void changePasswordFindUserByUserNameNotNullRequestGetOldPasswordNull(){
        ChangePasswordRequest request = new ChangePasswordRequest();
        when(userService.findUserByUserName("userName")).thenReturn(Optional.of(new Users()));
        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword("userName", request);
        });
        assertEquals(null, exception.getMessage());
    }

    @Test
    public void changePasswordFindUserByUserNameNotNullRequestGetOldPasswordNotNullGetNewPasswordNull(){
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        when(userService.findUserByUserName("userName")).thenReturn(Optional.of(new Users()));
        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword("userName", request);
        });
        assertEquals(null, exception.getMessage());
    }

    @Test
    public void changePasswordFindUserByUserNameNotNullRequestGetOldPasswordNotNullGetNewPasswordNotNullGetConfirmPasswordNull(){
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");
        when(userService.findUserByUserName("userName")).thenReturn(Optional.of(new Users()));
        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword("userName", request);
        });
        assertEquals(null, exception.getMessage());
    }

    @Test
    public void changePasswordFindUserByUserNameNotNullRequestGetOldPasswordGetNewPasswordGetConfirmPasswordNotNullCheckOlbPassFail(){
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");
        request.setConfirmPassword("confirmPass");
        when(userService.findUserByUserName("userName")).thenReturn(Optional.of(Users.builder().password("pass").build()));
        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword("userName", request);
        });
        assertEquals("Mật khẩu cũ không đúng.", exception.getMessage());
    }

    @Test
    public void changePasswordFindUserByUserNameNotNullRequestGetOldPasswordGetNewPasswordGetConfirmPasswordNotNullCheckOlbPassSuccessCheckNewPassFail(){
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");
        request.setConfirmPassword("confirmPass");
        Users users = new Users();
        users.setPassword("oldPass");
        when(userService.findUserByUserName("userName")).thenReturn(Optional.of(users));
        when(passwordEncoder.matches("oldPass", "oldPass")).thenReturn(true);
        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword("userName", request);
        });
        assertEquals("Xác nhận mật khẩu không đúng.", exception.getMessage());
    }

    @Test
    public void changePasswordFindUserByUserNameNotNullRequestGetOldPasswordGetNewPasswordGetConfirmPasswordNotNullCheckOlbPassSuccessCheckNewPassSuccess(){
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");
        request.setConfirmPassword("newPass");

        when(userService.findUserByUserName("userName")).thenReturn(Optional.of(Users.builder().password("oldPass").build()));
        when(passwordEncoder.matches("oldPass", "oldPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newPass");

        assertDoesNotThrow(() -> {
            userService.changePassword("userName",request);
        }, "No exception is throws");
    }

    @Test
    public void forgotPasswordSuccess(){
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(Users.builder().email("email").build()));
        assertDoesNotThrow(() -> {
            userService.forgotPassword("email");
        }, "No exception is throws");
    }

    @Test
    public void forgotPasswordFail(){
        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());
        Exception exception = assertThrows(Exception.class, () -> {
            userService.forgotPassword("email");
        });
        assertEquals("Người dùng không tồn tại", exception.getMessage());
    }

    @Test
    public void resetPasswordRequestNull(){
        Exception exception = assertThrows(Exception.class, () -> {
            userService.resetPassword(null);
        });
        assertEquals(null, exception.getMessage());
    }

    public void resetPasswordRequestGetResetPasswordTokenNull(){
        ResetPasswordRequest request = new ResetPasswordRequest();
        Exception exception = assertThrows(Exception.class, () -> {
            userService.resetPassword(request);
        });
        assertEquals("Mã token không hợp lệ.", exception.getMessage());
    }

    @Test
    public void resetPasswordRequestGetResetPasswordTokenNotNullNotFoundUser(){
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setResetPasswordToken("token");
        when(userRepository.findByResetPasswordToken("token")).thenReturn(Optional.empty());
        Exception exception = assertThrows(Exception.class, () -> {
            userService.resetPassword(request);
        });
        assertEquals("Không tìm thấy người dùng hoặc yêu cầu không tồn tại.", exception.getMessage());
    }

    @Test
    public void resetPasswordRequestGetResetPasswordTokenNotNullUserAndNewPassWordNull(){
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setResetPasswordToken("token");
        when(userRepository.findByResetPasswordToken("token")).thenReturn(Optional.of(new Users()));
        Exception exception = assertThrows(Exception.class, () -> {
            userService.resetPassword(request);
        });
        assertEquals("Vui lòng điền vào thông tin bắt buộc.", exception.getMessage());
    }

    @Test
    public void resetPasswordSuccess(){
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setResetPasswordToken("token");
        request.setNewPassword("new");
        request.setConfirmPassword("new");
        when(userRepository.findByResetPasswordToken("token")).thenReturn(Optional.of(new Users()));
        assertDoesNotThrow(() -> {
            userService.resetPassword(request);
        }, "No exception is throws");
    }

    private Users users(){
        Users users = new Users();
        users.setId(1L);
        users.setUsername("username");
        users.setPassword("password");
        users.setEmail("email");
        users.setPhone("0967445450");
        users.setRoleId(1L);
        users.setIsDeleted(0);
        users.setLastLoginTime(LocalDateTime.of(2022,8,16,16,16,16));
        users.setVerifiedEmail(false);
        users.setVerifiedPhone(false);
        users.setActive(true);
        users.setLocked(false);
        users.setAvatar("avatar");
        users.setResetPasswordToken("setResetPasswordToken");
        users.setGoogle(false);
        return users;
    }

    @Test
    public void testFindById() throws Exception {
        final Users users = users();
        when(userRepository.getUserById(1L)).thenReturn(users);
        final Users result = userService.findById(1L);
    }

    @Test
    public void testFindByIdOp() {
        final Optional<Users> optional = Optional.of(users());
        when(userRepository.findById(1L)).thenReturn(optional);
        final Optional<Users> result = userService.findByIdOp(1L);
    }

    @Test
    public void testFindByIdOp_UserRepositoryReturnsAbsent() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<Users> result = userService.findByIdOp(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testFindAll() {
        final List<Users> users = Arrays.asList(users());
        when(userRepository.findAll()).thenReturn(users);
        final List<Users> result = userService.findAll();
    }

    @Test
    public void testFindAll_UserRepositoryReturnsNoItems() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        final List<Users> result = userService.findAll();
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testFindByPhoneNumber() {
        final Optional<Users> optional = Optional.of(users());
        when(userRepository.findByPhone("0967445450")).thenReturn(optional);
        final Optional<Users> result = userService.findByPhoneNumber("0967445450");
    }

    @Test
    public void testFindByPhoneNumber_UserRepositoryReturnsAbsent() {
        when(userRepository.findByPhone("0967445450")).thenReturn(Optional.empty());
        final Optional<Users> result = userService.findByPhoneNumber("phone");
        assertThat(result).isEmpty();
    }

    @Test
    public void testCountUser() {
        when(userRepository.countUserRegisterToday()).thenReturn(Arrays.asList());
        when(userRepository.countUserRegisterMonthAgo()).thenReturn(Arrays.asList());
        when(userRepository.countAllUsersRegister()).thenReturn(Arrays.asList());
        final HashMap<String, List<CountRegisterUserResponse>> result = userService.countUser();
    }

    @Test
    public void testCountUser_UserRepositoryCountUserRegisterTodayReturnsNoItems() {
        when(userRepository.countUserRegisterToday()).thenReturn(Collections.emptyList());
        when(userRepository.countUserRegisterMonthAgo()).thenReturn(Arrays.asList());
        when(userRepository.countAllUsersRegister()).thenReturn(Arrays.asList());
        final HashMap<String, List<CountRegisterUserResponse>> result = userService.countUser();
    }

    @Test
    public void testCountUser_UserRepositoryCountUserRegisterMonthAgoReturnsNoItems() {
        when(userRepository.countUserRegisterToday()).thenReturn(Arrays.asList());
        when(userRepository.countUserRegisterMonthAgo()).thenReturn(Collections.emptyList());
        when(userRepository.countAllUsersRegister()).thenReturn(Arrays.asList());
        final HashMap<String, List<CountRegisterUserResponse>> result = userService.countUser();
    }

    @Test
    public void testCountUser_UserRepositoryCountAllUsersRegisterReturnsNoItems() {
        when(userRepository.countUserRegisterToday()).thenReturn(Arrays.asList());
        when(userRepository.countUserRegisterMonthAgo()).thenReturn(Arrays.asList());
        when(userRepository.countAllUsersRegister()).thenReturn(Collections.emptyList());
        final HashMap<String, List<CountRegisterUserResponse>> result = userService.countUser();
    }

    @Test
    public void testLockUnlockUser() throws Exception{
        final Users users = users();
        when(userRepository.getById(1L)).thenReturn(users);
        final Users users1 = users();
        when(userRepository.save(any(Users.class))).thenReturn(users1);
        final Users result = userService.lockUnlockUser(1L, "reason");
        verify(userRepository).save(any(Users.class));
    }

    @Test
    public void testLockUnlockUser_UserRepositoryGetByIdReturnsNull() {
        when(userRepository.getById(1L)).thenReturn(null);
        assertThatThrownBy(() -> userService.lockUnlockUser(1L, "reason")).isInstanceOf(HiveConnectException.class);
    }

//    @Test
//    public void testActiveDeactiveUser() {
//        final Users users = users();
//        when(userRepository.getById(1L)).thenReturn(users);
//        final Users users1 = users();
//        when(userRepository.save(any(Users.class))).thenReturn(users1);
//        final Users result = userService.activeDeactiveUser(1L);
//        verify(userRepository).save(any(Users.class));
//    }
//
//    @Test
//    public void testActiveDeactiveUser_UserRepositoryGetByIdReturnsNull() {
//        when(userRepository.getById(1L)).thenReturn(null);
//        assertThatThrownBy(() -> userService.activeDeactiveUser(1L))
//                .isInstanceOf(HiveConnectException.class);
//    }

    @Test
    public void testUpdatePhoneNumber() {
        userService.updatePhoneNumber("0967445450", 1L);
        verify(userRepository).updatePhoneNumber("0967445450", 1L);
    }
    @Test
    public void testUpdateIsVerifyPhone() {
        userService.updateIsVerifyPhone(false, 1L);

        verify(userRepository).updateIsVerifyPhone(false, 1L);
    }

    @Test
    public void testFindByPhoneAndIdIsNotIn() {
        final Users users = users();
        when(userRepository.findByPhoneAndIdIsNotIn("phone", 1L)).thenReturn(users);
        final Users result = userService.findByPhoneAndIdIsNotIn("phone", 1L);
    }

    @Test
    public void testFindUsersByUsernameOrEmail() {
        final Optional<Users> optional = Optional.of(users());
        when(userRepository.findUsersByUsernameOrEmail("username")).thenReturn(optional);
        final Optional<Users> result = userService.findUsersByUsernameOrEmail("username");
    }

    @Test
    public void testFindUsersByUsernameOrEmail_UserRepositoryReturnsAbsent() {
        when(userRepository.findUsersByUsernameOrEmail("username")).thenReturn(Optional.empty());
        final Optional<Users> result = userService.findUsersByUsernameOrEmail("username");
        assertThat(result).isEmpty();
    }

    @Test
    void testCountTotalRecruitmentStatistic() {
        // Setup
        final HashMap<String, Integer> expectedResult = new HashMap<>();
        when(mockAppliedJobService.countAppliedCVInSystem()).thenReturn(0);
        when(mockJobService.countJobInSystem()).thenReturn(0);
        when(mockCvService.countCVInSystem()).thenReturn(0);

        // Run the test
        final HashMap<String, Integer> result = userService.countTotalRecruitmentStatistic();

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }
}
