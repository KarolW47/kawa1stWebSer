package pl.webser.service;

import org.springframework.beans.factory.annotation.Autowired;
import pl.webser.model.Role;
import pl.webser.repository.RoleRepository;

public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Boolean isRoleAlreadyExists(String roleName) {
        return roleRepository.existsByRoleName(roleName);
    }

    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    public Role getRoleByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
