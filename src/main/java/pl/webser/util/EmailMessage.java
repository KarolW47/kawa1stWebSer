package pl.webser.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import pl.webser.model.User;

@Component
public class EmailMessage {

    @Value("${support.email}")
    private String supportEmail;

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }

    public SimpleMailMessage createResetPasswordTokenEmail(String contextPath, String token, User user) {
        final String url = contextPath + "/user/change_password?username=" + user.getUsername() + "&token=" + token;
        final String message = "Reset your password by clicking link:";
        final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmailAddress());
        simpleMailMessage.setSubject("Reset Password");
        simpleMailMessage.setText(message + " \r\n" + url);
        simpleMailMessage.setFrom(supportEmail);
        return simpleMailMessage;
    }
}
