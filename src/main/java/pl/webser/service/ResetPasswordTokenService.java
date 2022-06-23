package pl.webser.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.webser.model.ResetPasswordToken;
import pl.webser.model.User;
import pl.webser.repository.ResetPasswordTokenRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class ResetPasswordTokenService {

    @Value("${reset.password.expiration.time}")
    private long resetPasswordTokenExpirationTime;

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    public ResetPasswordTokenService(ResetPasswordTokenRepository resetPasswordTokenRepository) {
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
    }

    public String createResetPasswordToken(User user) {
        String token = UUID.randomUUID().toString();
        log.info("UUID token: {}", token);
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken(token);
        resetPasswordToken.setUser(user);
        resetPasswordToken.setExpirationDate(new Date(System.currentTimeMillis() + resetPasswordTokenExpirationTime));
        resetPasswordTokenRepository.save(resetPasswordToken);
        return token;
    }

    public User getUserByResetPasswordTokenSignedTo(String token) {
        return resetPasswordTokenRepository.findByToken(token).getUser();
    }

    public Boolean isTokenFound(String token) {
        return resetPasswordTokenRepository.existsByToken(token);
    }

    public Boolean isTokenExpired(String token) {
        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(token);
        return resetPasswordToken.getExpirationDate().after(new Date(System.currentTimeMillis()));
    }
}
