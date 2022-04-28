package pl.webser.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.webser.model.Role;
import pl.webser.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUsername(String username);

    Boolean existsByEmailAddress(String emailAddress);

    User findByUsername(String username);

    @Modifying
    void deleteByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.username = ?1 WHERE u.username = ?2")
    void updateUserUsernameByUsername(String passedUsername, String oldUsername);

    @Modifying
    @Query("UPDATE User u SET u.password = ?1 WHERE u.username = ?2")
    void updateUserPasswordByUsername(String password, String username);

    @Modifying
    @Query("UPDATE User u SET u.aboutMeInfo = ?1 WHERE u.username = ?2")
    void updateUserAboutMeInfoByUsername(String aboutMeInfo, String username);

    @Modifying()
    @Query("UPDATE User u Set u = ?1 WHERE u.id = ?2")
    void updateUserWithNewRoleList(User user, Long id);

}