package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.common.CreateRoleUserRequest;
import fpt.edu.capstone.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role getRoleById(long roleId);

    Optional<Role> findRoleById(long roleId);

    Role createRole(CreateRoleUserRequest request);

    List<Role> getAllRole();
}
