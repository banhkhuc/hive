package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.common.CreateRoleUserRequest;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.entity.Role;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.RoleRepository;
import fpt.edu.capstone.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRoleById(long roleId) {
        return roleRepository.getById(roleId);
    }

    @Override
    public Optional<Role> findRoleById(long roleId) {
        return roleRepository.findById(roleId);
    }

    @Override
    public Role createRole(CreateRoleUserRequest request) {
        Role role = new Role();
        if(request.getRoleName() == null || request.getRoleName().trim().isEmpty()) {
            throw new HiveConnectException(ResponseMessageConstants.REQUIRE_INPUT_MANDATORY_FIELD);
        }
        if(roleRepository.getByRoleName(request.getRoleName()) != null) {
            throw new HiveConnectException(ResponseMessageConstants.ROLE_NAME_EXISTS);
        }
        role.setName(request.getRoleName());
        role.setDescription(request.getDescription());
        roleRepository.save(role);
        return role;
    }

    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }
}
