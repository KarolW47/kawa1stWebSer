package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.webser.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r FROM Role r WHERE r.roleName = ?1")
    Role findByRoleName(String roleName);

    @Query("SELECT CASE WHEN EXISTS (SELECT r.roleName FROM Role r WHERE r.roleName = ?1) THEN true ELSE false END")
    Boolean existsByRoleName(String roleName);
}
