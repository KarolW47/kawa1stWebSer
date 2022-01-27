package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.webser.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);
    Boolean existsByEmailAddress(String emailAddress);
    User findByUsername(String username);
}
