package pl.webser.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.webser.model.ResetPasswordToken;
import pl.webser.model.User;
import pl.webser.repository.ResetPasswordTokenRepository;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class ResetPasswordTokenService {

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    public ResetPasswordTokenService(ResetPasswordTokenRepository resetPasswordTokenRepository){
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
    }

    public String createResetPasswordToken(User user){
        String token = UUID.randomUUID().toString();
        log.info("UUID token: {}",token);
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken(token);
        resetPasswordToken.setUser(user);
        resetPasswordTokenRepository.save(resetPasswordToken);
        return token;
    }
}
