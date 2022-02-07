package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.webser.enumeration.RoleEnum;
import pl.webser.model.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(RoleEnum roleName);
}
