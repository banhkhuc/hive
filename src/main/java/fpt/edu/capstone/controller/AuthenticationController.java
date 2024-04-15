package fpt.edu.capstone.controller;

import fpt.edu.capstone.common.CommonValidator;
import fpt.edu.capstone.common.user.GooglePojo;
import fpt.edu.capstone.common.user.GoogleUtils;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.login.LoginGoogleRequest;
import fpt.edu.capstone.dto.login.LoginRequest;
import fpt.edu.capstone.dto.login.UserInforResponse;
import fpt.edu.capstone.dto.register.ChangePasswordRequest;
import fpt.edu.capstone.dto.register.RegisterGoogleRequest;
import fpt.edu.capstone.dto.register.RegisterRequest;
import fpt.edu.capstone.dto.register.ResetPasswordRequest;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.UserRepository;
import fpt.edu.capstone.security.TokenUtils;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.service.impl.SecurityUserServiceImpl;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.LogUtils;
import fpt.edu.capstone.utils.ResponseData;
import fpt.edu.capstone.utils.ResponseDataUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final CandidateService candidateService;

    private final RecruiterService recruiterService;

    private final AdminService adminService;

    private final AuthenticationManager authenticationManager;

    private final SecurityUserServiceImpl securityUserService;

    private final PasswordEncoder passwordEncoder;

    private final TokenUtils jwtTokenUtil;

    private final ConfirmTokenService confirmTokenService;

    private final UserRepository userRepository;

    private final RequestJoinCompanyService requestJoinCompanyService;

    private final CommonValidator commonValidator;

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseDataUser login(@RequestBody @Valid LoginRequest request) {
        try {
            Optional<Users> userByUserName = userService.findUsersByUsernameOrEmail(request.getUsername());
            Optional<Users> userByEmailGmail = userService.findByEmail(request.getUsername() + "@gmail.com");
            Optional<Users> userByEmailYopMail = userService.findByEmail(request.getUsername() + "@yopmail.com");
            Optional<Users> users;
            if (userByEmailGmail.isPresent()) {
                users = userByEmailGmail;
            } else if (userByEmailYopMail.isPresent()) {
                users = userByEmailYopMail;
            } else if (userByUserName.isPresent()) {
                users = userByUserName;
            } else {
                throw new HiveConnectException(ResponseMessageConstants.ACCOUNT_DOES_NOT_EXIST
                        + " " + ResponseMessageConstants.LOGIN_FAILED);
            }
            if (users.isPresent()) {
                if (users.get().isLocked()) {
                    throw new HiveConnectException(ResponseMessageConstants.USER_HAS_BEEN_LOCKED);
                }
            }
            if (!users.isPresent()) {
                throw new HiveConnectException(ResponseMessageConstants.ACCOUNT_DOES_NOT_EXIST
                        + " " + ResponseMessageConstants.LOGIN_FAILED);
            }
            if (users.get().isGoogle()) {
                throw new HiveConnectException(ResponseMessageConstants.ACCOUNT_DOES_NOT_EXIST
                        + " " + ResponseMessageConstants.LOGIN_FAILED);
            }
            authenticate(request.getUsername().trim(), request.getPassword());
            String username = request.getUsername();
            logger.info("login with username {}", username);
            if (StringUtils.containsWhitespace(username) || StringUtils.containsWhitespace(request.getPassword())) {
                return new ResponseDataUser(Enums.ResponseStatus.ERROR.getStatus(),
                        ResponseMessageConstants.USERNAME_OR_PASSWORD_MUST_NOT_CONTAIN_ANY_SPACE_CHARACTERS);
            }

            final UserDetails userDetails = securityUserService.loadUserByUsername(username);

            Optional<Users> optionalUser = userService.findUsersByUsernameOrEmail(username);
            if (!optionalUser.isPresent()) {
                throw new HiveConnectException("Tên đăng nhập: " + username + "không tìm thấy.");
            }
            Users user = optionalUser.get();
            String token = jwtTokenUtil.generateToken(userDetails);

            user.setLastLoginTime(LocalDateTime.now());
            userService.saveUser(user);

            UserInforResponse response = new UserInforResponse();
            response.setUser(user);

            if (user.getRoleId() == 3) {
                Optional<Candidate> candidate = candidateService.findCandidateByUserId(user.getId());
                if (!candidate.isPresent()) {
                    throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
                }
                response.setCandidate(candidate.get());
            }
            if (user.getRoleId() == 2) {
                Optional<Recruiter> recruiter = recruiterService.findRecruiterByUserId(user.getId());
                if (!recruiter.isPresent()) {
                    throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
                }
                if (recruiter.get().getCompanyId() == 0) {
                    response.setJoinedCompany(false);
                    Optional<RequestJoinCompany> requestJoinCompany = requestJoinCompanyService
                            .getSentRequest(recruiter.get().getId());
                    if (requestJoinCompany.isPresent()) {
                        response.setCreatedOrRequestedJoinCompany(true);
                    } else {
                        response.setCreatedOrRequestedJoinCompany(false);
                    }
                } else if (recruiter.get().getCompanyId() > 0) {
                    response.setJoinedCompany(true);
                    response.setCreatedOrRequestedJoinCompany(true);
                }
                if (recruiter.get().getBusinessLicenseApprovalStatus() != null
                        && recruiter.get().getBusinessLicenseApprovalStatus()
                        .equals(Enums.ApprovalStatus.APPROVED.getStatus())) {
                    response.setApprovedBusinessLicense(true);
                } else {
                    response.setApprovedBusinessLicense(false);
                }
                response.setRecruiter(recruiter.get());
            }
            if (user.getRoleId() == 1) {
                Optional<Admin> admin = adminService.findAdminByUserId(user.getId());
                if (!admin.isPresent()) {
                    throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
                }
                response.setAdmin(admin.get());
            }

            if (!user.isVerifiedEmail()) {
                response.setVerifiedEmail(false);
            } else {
                response.setVerifiedEmail(true);
            }
            return new ResponseDataUser(Enums.ResponseStatus.SUCCESS.getStatus(),
                    ResponseMessageConstants.LOGIN_SUCCESS, response, token);

        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseDataUser(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            throw new Exception(ResponseMessageConstants.USER_HAS_BEEN_LOCKED, e);
        } catch (BadCredentialsException e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            throw new Exception(ResponseMessageConstants.USERNAME_OR_PASSWORD_INCORRECT, e);
        }
    }

    @GetMapping("/check-email-registered")
    public ResponseData checkEmailRegistered(@RequestParam String email) {
        try {
            boolean isRegistered = userRepository.findByEmail(email).isPresent();
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, isRegistered);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    //TODO : namnh
    @PostMapping("/login-google")
    public ResponseDataUser loginGoogle(@RequestBody LoginGoogleRequest request) {
        try {
            GooglePojo googlePojo = new GooglePojo();
            googlePojo.setEmail(request.getEmail());
            googlePojo.setName(request.getName());
            googlePojo.setPicture(request.getPicture());

            if (StringUtils.containsWhitespace(request.getEmail())) {
                return new ResponseDataUser(Enums.ResponseStatus.ERROR.getStatus(),
                        ResponseMessageConstants.REQUIRE_INPUT_MANDATORY_FIELD);
            }

            String email = request.getEmail();
            String[] s = email.split("@");
            String username = s[0];

            //đã có tài khoản Hive Connect với Google account này
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                //tìm user theo email google trả về
                Optional<Users> user = userService.findByEmail(request.getEmail());
                if (user.isPresent()) {
                    if (user.get().isLocked()) {
                        throw new HiveConnectException(ResponseMessageConstants.USER_HAS_BEEN_LOCKED);
                    }
                }
                //thực hiện login bình thường
                user.get().setLastLoginTime(LocalDateTime.now());
                user.get().setPassword(passwordEncoder.encode("1"));
                userService.saveUser(user.get());
                //lấy token
                logger.info("login with username {}", username);
                Users emailExistsButUseForAnotherAccount = userRepository.findByUsernameAndEmail(username, email);
                if (emailExistsButUseForAnotherAccount == null) {
                    throw new HiveConnectException(ResponseMessageConstants.EMAIL_EXISTS);
                }
                final UserDetails userDetails = securityUserService.loadUserByUsername(username);

                String token = jwtTokenUtil.generateToken(userDetails);
                //trả data cho FE
                UserInforResponse response = new UserInforResponse();
                response.setUser(user.get());
                if (user.get().getRoleId() == 3) {
                    Optional<Candidate> candidate = candidateService.findCandidateByUserId(user.get().getId());
                    if (!candidate.isPresent()) {
                        throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
                    }
                    response.setCandidate(candidate.get());
                }
                if (user.get().getRoleId() == 2) {
                    Optional<Recruiter> recruiter = recruiterService.findRecruiterByUserId(user.get().getId());
                    if (!recruiter.isPresent()) {
                        throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
                    }
                    if (recruiter.get().getCompanyId() == 0) {
                        response.setJoinedCompany(false);
                    } else if (recruiter.get().getCompanyId() > 0) {
                        response.setJoinedCompany(true);
                    }
                    if (recruiter.get().getBusinessLicenseApprovalStatus() != null
                            && recruiter.get().getBusinessLicenseApprovalStatus()
                            .equals(Enums.ApprovalStatus.APPROVED.getStatus())) {
                        response.setApprovedBusinessLicense(true);
                    } else {
                        response.setApprovedBusinessLicense(false);
                    }
                    response.setRecruiter(recruiter.get());
                }
                if (!user.get().isVerifiedEmail()) {
                    response.setVerifiedEmail(false);
                } else {
                    response.setVerifiedEmail(true);
                }
                return new ResponseDataUser(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.LOGIN_SUCCESS, response, token);
                //chưa có tài khoản Hive Connect với Google account này
            } else {
                //lưu user vào database table user
                RegisterGoogleRequest registerRequest = modelMapper.map(request, RegisterGoogleRequest.class);
                userService.registerGoogleUser(registerRequest);
                //lưu theo role
                Users user = userService.getByUserName(username);
                if (user.getRoleId() == 3) {
                    candidateService.insertGoogleCandidate(googlePojo, user);
                }
                if (user.getRoleId() == 2) {
                    recruiterService.insertGoogleRecruiter(googlePojo, user);
                }
                //không có authen của security
                ConfirmToken confirmToken = new ConfirmToken(user.getId());
                // Generate token and save to DB
                confirmTokenService.saveConfirmToken(confirmToken);
                // Cần lấy ra token để truyền vào url cho verify
                ConfirmToken cf = confirmTokenService.getByUserId(user.getId());
                String mailToken = cf.getConfirmationToken();
                confirmTokenService.verifyEmailUser(request.getEmail(), mailToken);

                UserDetails userDetails = securityUserService.loadUserByUsername(user.getUsername());
                String jwtToken = jwtTokenUtil.generateToken(userDetails);
                return new ResponseDataUser(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.SUCCESS, user, jwtToken);
            }
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseDataUser(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/register")
    @Operation(summary = "register user")
    @Transactional(rollbackOn = HiveConnectException.class)
    public ResponseDataUser register(@RequestBody RegisterRequest request) throws Exception {
        try {
            String password = request.getPassword();
            String email = request.getEmail().trim();
            String username = email.substring(0, email.indexOf("@"));
            String fullName = request.getFullName().trim();
            if (StringUtils.containsWhitespace(username) || StringUtils.containsWhitespace(password)) {
                return new ResponseDataUser(Enums.ResponseStatus.ERROR.getStatus(),
                        ResponseMessageConstants.USERNAME_OR_PASSWORD_MUST_NOT_CONTAIN_ANY_SPACE_CHARACTERS);
            }
            userService.registerUser(new RegisterRequest(username, password, request.getConfirmPassword(), email,
                    request.getPhone(), request.getRoleId(), request.getFullName()));
            Users user = userService.getByUserName(username);
            if (user.getRoleId() == 3) {
                candidateService.insertCandidateForRegister(user.getId(), fullName);
            }
            if (user.getRoleId() == 2) {
                recruiterService.insertRecruiterForRegister(user.getId(), fullName);
            }
            //1 super admin | 4 counselors
            if (user.getRoleId() == 1 || user.getRoleId() == 4) {
                adminService.insertAdmin(user.getId(), fullName);
            }
            final UserDetails userDetails = securityUserService.loadUserByUsername(username);

            //region: Handle verify email
            ConfirmToken confirmToken = new ConfirmToken(user.getId());
            confirmTokenService.saveConfirmToken(confirmToken);
            ConfirmToken cf = confirmTokenService.getByUserId(user.getId());
            String mailToken = cf.getConfirmationToken();
            confirmTokenService.verifyEmailUser(email, mailToken);
            //endregion
            String jwtToken = jwtTokenUtil.generateToken(userDetails);
            return new ResponseDataUser(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.REGISTER_SUCCESS, user, jwtToken);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseDataUser(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/resend-email/{email}")
    @Operation(summary = "resend email")
    public ResponseData resendEmail(@PathVariable("email") String email) {
        try {
            if (email.trim().isEmpty() || !commonValidator.validate(email)) {
                throw new HiveConnectException(ResponseMessageConstants.EMAIL_INVALID);
            }
            Optional<Users> user = userService.findByEmail(email);
            if (!user.isPresent()) {
                throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
            }
            if (user.get().isVerifiedEmail()) {
                throw new HiveConnectException(ResponseMessageConstants.EMAIL_VERIFIED);
            }
            ConfirmToken cf = confirmTokenService.getByUserId(user.get().getId());
            String mailToken = cf.getConfirmationToken();
            confirmTokenService.verifyEmailUser(user.get().getEmail(), mailToken);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.RESEND_EMAIL_SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/confirm-account")
    @Operation(summary = "confirm account")
    public ResponseDataUser confirmAccount(@RequestParam("token") String token) {
        try {
            ConfirmToken cf = confirmTokenService.getByConfirmToken(token);
            if (cf == null) {
                throw new HiveConnectException("Token invalid");
            }
            String mailToken = cf.getConfirmationToken();
            Users user = userService.getUserById(cf.getUserId());
            if (StringUtils.equals(token, mailToken)) {
                user.setVerifiedEmail(true);
                user.setActive(true);
            }
            userService.saveUser(user);
            final UserDetails userDetails = securityUserService.loadUserByUsername(user.getUsername());
            String tokenJwt = jwtTokenUtil.generateToken(userDetails);
            return new ResponseDataUser(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.REGISTER_SUCCESS, user, tokenJwt);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseDataUser(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/password/{username}")
    @Operation(summary = "change password user")
    public ResponseData changePassword(@PathVariable(name = "username") String username,
                                       @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(username, request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.CHANGE_PASSWORD_SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    /*
     * @author Mai
     */
    // Sending a simple Email
//    @PostMapping("/sendMail")
//    @Operation(summary = "send email")
//    public ResponseData sendMail(@RequestBody EmailDetails details) {
//        try {
//            String status = emailService.sendSimpleMail(details);
//            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), status);
//        } catch (Exception e) {
//            String msg = LogUtils.printLogStackTrace(e);
//            logger.error(msg);
//            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), "Error while Sending Mail!!!");
//        }
//    }

    /*
     * @author Mai
     */
    // Sending email with attachment
//    @PostMapping("/sendMailWithAttachment")
//    public ResponseData sendMailWithAttachment(@RequestBody EmailDetails details) {
//        try {
//            String status = emailService.sendMailWithAttachment(details);
//            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), status);
//        } catch (Exception e) {
//            String msg = LogUtils.printLogStackTrace(e);
//            logger.error(msg);
//            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), "Error while Sending Mail!!");
//        }
//    }

    @PostMapping("/forgot-password")
    @Operation(summary = "user forgot password, system will send a mail to user's email with reset password token link")
    public ResponseData processForgotPassword(@RequestParam String email) {
        try {
            userService.forgotPassword(email);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Chúng tôi đã gửi mail làm mới mật khẩu tới " + email + ", vui lòng kiểm tra.");
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "reset password for function forgot password")
    public ResponseData processResetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                    ResponseMessageConstants.CHANGE_PASSWORD_SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }
}
