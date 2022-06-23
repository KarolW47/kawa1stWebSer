package pl.webser.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import pl.webser.model.User;

import java.util.Locale;

@Component
public class EmailMessage {

    private final MessageSource messageSource;
    private final Environment environment;

    @Autowired
    public EmailMessage(MessageSource messageSource, Environment environment){
        this.messageSource = messageSource;
        this.environment = environment;
    }

    public SimpleMailMessage createResetPasswordTokenEmail(String contextPath, String token, User user){
        final String url = contextPath + "/user/change_password?username=" + user.getUsername() + "&token=" + token;
        final String message = "Reset your password by clicking link:";
        final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmailAddress());
        simpleMailMessage.setSubject("Reset Password");
        simpleMailMessage.setText(message + " \r\n" + url);
        simpleMailMessage.setFrom(environment.getProperty("support.email"));
        return simpleMailMessage;
    }
}
