package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.webser.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUsername(String username);

    Boolean existsByEmailAddress(String emailAddress);

    User findByUsername(String username);

    User findByEmailAddress(String emailAddress);

    @Modifying
    void deleteByEmailAddress(String emailAddress);

    @Modifying
    @Query("UPDATE User u SET u.username = ?1 WHERE u.username = ?2")
    void updateUserUsernameByUsername(String passedUsername, String oldUsername);

    @Modifying
    @Query("UPDATE User u SET u.password = ?1 WHERE u.emailAddress = ?2")
    void updateUserPasswordByEmailAddress(String password, String emailAddress);

    @Modifying
    @Query("UPDATE User u SET u.aboutMeInfo = ?1 WHERE u.emailAddress = ?2")
    void updateUserAboutMeInfoByEmailAddress(String aboutMeInfo, String emailAddress);

    @Modifying
    @Query("UPDATE User u Set u = ?1 WHERE u.id = ?2")
    void updateUserWithNewRoleList(User user, Long id);

}