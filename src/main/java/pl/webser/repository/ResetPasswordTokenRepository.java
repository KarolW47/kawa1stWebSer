package pl.webser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.webser.model.ResetPasswordToken;
import pl.webser.model.User;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {

    Boolean existsByToken(String token);

    ResetPasswordToken findByToken(String token);

}
