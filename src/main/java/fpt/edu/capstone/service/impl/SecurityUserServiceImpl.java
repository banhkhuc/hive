package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Role;
import fpt.edu.capstone.entity.Users;
import fpt.edu.capstone.repository.UserRepository;
import fpt.edu.capstone.service.RoleService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityUserServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    private final RoleService roleService;

    public SecurityUserServiceImpl(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> optionUser = userRepository.findUsersByUsernameOrEmail(username);
        if (!optionUser.isPresent()) {
            throw new UsernameNotFoundException("Không tìn thấy người dùng với tên đăng nhập: " + username);
        }
        Users user = optionUser.get();
        Role role = roleService.getRoleById(user.getRoleId());

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(role.getName())
                .accountExpired(false).accountLocked(false)
                .credentialsExpired(false).disabled(false)
                .build();
    }
}