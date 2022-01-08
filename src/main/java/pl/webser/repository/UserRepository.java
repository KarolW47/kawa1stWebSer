package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.webser.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailAddress(String emailaddress);
}
