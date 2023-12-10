package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.common.CreateRoleUserRequest;
import fpt.edu.capstone.entity.Role;
import fpt.edu.capstone.repository.RoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceImplTest {

    @Mock
    private RoleRepository mockRoleRepository;

    private RoleServiceImpl roleServiceImplUnderTest;

    @Before
    public void setUp() {
        roleServiceImplUnderTest = new RoleServiceImpl(mockRoleRepository);
    }
    
    private Role role(){
        Role role = new Role();
        role.setId(1L);
        role.setName("name");
        role.setDescription("description");
        return role;
    }
    
    @Test
    public void testGetRoleById() {
        when(mockRoleRepository.getById(1L)).thenReturn(role());
        final Role result = roleServiceImplUnderTest.getRoleById(1L);
    }

    @Test
    public void testFindRoleById() {
        final Optional<Role> optional = Optional.of(role());
        when(mockRoleRepository.findById(1L)).thenReturn(optional);
        final Optional<Role> result = roleServiceImplUnderTest.findRoleById(1L);
    }

    @Test
    public void testFindRoleById_RoleRepositoryReturnsAbsent() {
        when(mockRoleRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<Role> result = roleServiceImplUnderTest.findRoleById(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testCreateRole() {
        final CreateRoleUserRequest request = new CreateRoleUserRequest("role", "description");
        when(mockRoleRepository.save(any(Role.class))).thenReturn(role());
        final Role result = roleServiceImplUnderTest.createRole(request);
        verify(mockRoleRepository).save(any(Role.class));
    }

    @Test
    public void testCreateRole_RoleRepositoryGetByRoleNameReturnsNull() {
        final CreateRoleUserRequest request = new CreateRoleUserRequest("name", "description");
        when(mockRoleRepository.getByRoleName("name")).thenReturn(null);
        when(mockRoleRepository.save(any(Role.class))).thenReturn(role());
        final Role result = roleServiceImplUnderTest.createRole(request);
        verify(mockRoleRepository).save(any(Role.class));
    }

    @Test
    public void testGetAllRole() {
        final List<Role> roleList = Arrays.asList(role());
        when(mockRoleRepository.findAll()).thenReturn(roleList);
        final List<Role> result = roleServiceImplUnderTest.getAllRole();
    }

    @Test
    public void testGetAllRole_RoleRepositoryReturnsNoItems() {
        when(mockRoleRepository.findAll()).thenReturn(Collections.emptyList());
        final List<Role> result = roleServiceImplUnderTest.getAllRole();
        assertThat(result).isEqualTo(Collections.emptyList());
    }
}
