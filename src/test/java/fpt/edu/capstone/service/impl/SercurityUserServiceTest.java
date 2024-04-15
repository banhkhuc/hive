package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Role;
import fpt.edu.capstone.entity.Users;
import fpt.edu.capstone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class SercurityUserServiceTest {

  @Mock
  UserRepository userRepository;

  @Mock
  RoleServiceImpl roleService;

  @InjectMocks
  SecurityUserServiceImpl securityUserService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void loadUserByUsernameWithoutUser(){
    Optional<Users> opUser = Optional.empty();
    when(userRepository.findUsersByUsernameOrEmail("username")).thenReturn(opUser);
    Exception exception = assertThrows(Exception.class, () -> {
      securityUserService.loadUserByUsername("username");
    });
    assertEquals("Không tìn thấy người dùng với tên đăng nhập: username", exception.getMessage());
  }

  @Test
  public void loadUserByUsernameHaveUserWithUserNameTrue() {
    Users user = Users.builder()
        .id(1L)
        .username("userName")
        .password("pass")
        .roleId(1L)
        .build();
    Optional<Users> opUser = Optional.of(user);
    when(userRepository.findUsersByUsernameOrEmail("userName")).thenReturn(opUser);

    Role role = new Role();
    role.setId(1L);
    role.setName("role");
    role.setDescription("");
    when(roleService.getRoleById(user.getRoleId())).thenReturn(role);

    assertEquals("userName", securityUserService.loadUserByUsername("userName").getUsername());
  }

  @Test
  public void loadUserByUsernameHaveUserWithPassTrue() {
    Users user = Users.builder()
        .id(1L)
        .username("userName")
        .password("pass")
        .roleId(1L)
        .build();
    Optional<Users> opUser = Optional.of(user);
    when(userRepository.findUsersByUsernameOrEmail("userName")).thenReturn(opUser);

    Role role = new Role();
    role.setId(1L);
    role.setName("role");
    role.setDescription("");
    when(roleService.getRoleById(user.getRoleId())).thenReturn(role);

    assertEquals("pass", securityUserService.loadUserByUsername("userName").getPassword());
  }

  @Test
  public void loadUserByUsernameHaveUserWithRoleTrue() {
    Users user = Users.builder()
        .id(1L)
        .username("userName")
        .password("pass")
        .roleId(1L)
        .build();
    Optional<Users> opUser = Optional.of(user);
    when(userRepository.findUsersByUsernameOrEmail("userName")).thenReturn(opUser);

    Role role = new Role();
    role.setId(1L);
    role.setName("role");
    role.setDescription("");
    when(roleService.getRoleById(user.getRoleId())).thenReturn(role);



    assertEquals(org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
        .password(user.getPassword())
        .authorities(role.getName())
        .accountExpired(false).accountLocked(false)
        .credentialsExpired(false).disabled(false)
        .build(), securityUserService.loadUserByUsername("userName"));
  }

}
