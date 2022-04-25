package pl.webser.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.webser.model.Role;
import pl.webser.repository.RoleRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
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

    public List<Role> getAllRoles(){
        return roleRepository.findAll();
    }
}
