package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.webser.model.Role;
import pl.webser.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT CASE WHEN EXIST (SELECT u.username FROM User u WHERE u.username = ?1) THEN true ELSE false END")
    Boolean existsByUsername(String username);

    @Query("SELECT CASE WHEN EXIST (SELECT u.emailAddress FROM User u WHERE u.emailAddress =?1) THEN true ELSE false " +
            "END")
    Boolean existsByEmailAddress(String emailAddress);

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);

    @Modifying
    @Query("DELETE FROM User u WHERE u.username = ?1")
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

    @Modifying
    @Query("UPDATE USER u SET u.userRoles = ?1 WHERE u.id = ?2")
    void updateUserRolesById(List<Role> userRoles, Long id);

}