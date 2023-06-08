package pl.webser.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import pl.webser.model.Role;
import pl.webser.model.User;
import pl.webser.security.JWTUtil;
import pl.webser.service.ResetPasswordTokenService;
import pl.webser.service.UserService;
import pl.webser.util.EmailMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.webser.security.filter.CustomAuthenticationFilter.USER_ID;
import static pl.webser.security.filter.CustomAuthorizationFilter.ACCESS_TOKEN_HEADER;
import static pl.webser.security.filter.CustomAuthorizationFilter.REFRESH_TOKEN_HEADER;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final ResetPasswordTokenService resetPasswordTokenService;
    private final JavaMailSender javaMailSender;
    private final EmailMessage emailMessage;

    @Autowired
    public UserController(
            UserService userService,
            JWTUtil jwtUtil,
            ResetPasswordTokenService resetPasswordTokenService,
            JavaMailSender javaMailSender,
            EmailMessage emailMessages) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.resetPasswordTokenService = resetPasswordTokenService;
        this.javaMailSender = javaMailSender;
        this.emailMessage = emailMessages;
    }

    @GetMapping(path = "/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        if (userService.isUsernameTaken(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    "Username already exists.");
        } else if (!userService.isUsernameValid(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    "Username does not fit into required pattern.");
        } else if (userService.isEmailAddressTaken(user.getEmailAddress())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    "Account with this email address already exists.");
        } else if (!userService.isEmailAddressValid(user.getEmailAddress())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    "Email address does not fit into required pattern.");
        } else if (!userService.isPasswordValid(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    "Password does not fit into required pattern.");
        } else {
            log.info("Successfully added user with email: " + user.getEmailAddress() + " to DB.");
            userService.saveProvidedUser(user);
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(path = "/profile")
    public ResponseEntity<Optional<User>> getSpecificUser(@RequestParam(name = "user_id") String userId) {
        Long id = Long.valueOf(userId);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping(path = "/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);
        if (refreshToken != null) {
            try {
                User user = userService.getUserByEmailAddress(jwtUtil.getEmailAddressFromJwtToken(refreshToken));
                log.info("Refreshing token for user with email: {}, in progress.", user.getEmailAddress());
                List<String> roles = user.getUserRoles()
                        .stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toList());

                response.setHeader(ACCESS_TOKEN_HEADER, jwtUtil.generateJwtToken(user.getEmailAddress(), roles));
                response.setHeader(REFRESH_TOKEN_HEADER, jwtUtil.generateJwtRefreshToken(user.getEmailAddress()));
                response.setHeader(USER_ID, String.valueOf(user.getId()));
                log.info("Refreshing token for user with email: {}, done.", user.getEmailAddress());
            } catch (Exception exception) {
                log.info("Error in refreshing token for user with email: {}, occurred.",
                        jwtUtil.getEmailAddressFromJwtToken(refreshToken));
                response.sendError(HttpStatus.FORBIDDEN.value());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setHeader("error", exception.getMessage());
                log.info("Exception message: {}.", exception.getMessage());
            }
        } else throw new RuntimeException("Refresh Token is missing.");
    }

    @DeleteMapping(path = "/profile/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader(name = ACCESS_TOKEN_HEADER) String token,
                                        @RequestParam(name = "confirmationPassword") String confirmationPassword) {
        String emailAddress = jwtUtil.getEmailAddressFromJwtToken(token);
        if (!userService.isProvidedPasswordEqualToStored(confirmationPassword, emailAddress)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong present password.");
        } else {
            userService.deleteSpecificUser(emailAddress);
            return ResponseEntity.ok().build();
        }
    }

    @PatchMapping(path = "/profile/username/change")
    public ResponseEntity<?> changeUsername(@RequestHeader(name = ACCESS_TOKEN_HEADER) String token,
                                            @RequestBody String passedUsername) {
        User user = userService.getUserByEmailAddress(jwtUtil.getEmailAddressFromJwtToken(token));
        if (userService.isUsernameTaken(passedUsername)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Username already exists.");
        } else if (!userService.isUsernameValid(passedUsername)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    "Username does not fit into required pattern.");
        } else {
            userService.changeUsernameOfSpecificUser(passedUsername, user.getUsername(), user.getEmailAddress());
            return ResponseEntity.ok().build();
        }
    }

    @PatchMapping(path = "/profile/password/change")
    public ResponseEntity<?> changePassword(@RequestHeader(name = ACCESS_TOKEN_HEADER) String token,
                                            @RequestBody String[] passwords) {
        String emailAddress = jwtUtil.getEmailAddressFromJwtToken(token);
        String passedConfirmationPassword = passwords[0];
        String passedNewPassword = passwords[1];

        if (!userService.isProvidedPasswordEqualToStored(passedConfirmationPassword, emailAddress)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong present password.");
        } else if (!userService.isPasswordValid(passedNewPassword)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    "Password does not fit into required pattern.");
        } else {
            userService.changePasswordOfSpecificUser(passedNewPassword, emailAddress);
            return ResponseEntity.ok().build();
        }
    }

    @PatchMapping(path = "/profile/about_me_info/change")
    public ResponseEntity<?> changeAboutMeInfo(@RequestHeader(name = ACCESS_TOKEN_HEADER) String token,
                                               @RequestBody String passedAboutMeInfo) {
        String emailAddress = jwtUtil.getEmailAddressFromJwtToken(token);
        userService.changeAboutMeInfoOfSpecificUser(passedAboutMeInfo, emailAddress);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/reset_password")
    public ResponseEntity<?> resetPassword(HttpServletRequest request, @RequestBody String emailAddress) {
        User user = userService.getUserByEmailAddress(emailAddress);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with that email does not exist.");
        } else {
            String token = resetPasswordTokenService.createResetPasswordToken(user);
            String url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            SimpleMailMessage simpleMailMessage =
                    emailMessage.createResetPasswordTokenEmail(url, token, user);
            javaMailSender.send(simpleMailMessage);
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(path = "/change_password")
    public ResponseEntity<?> validateResetPasswordTokenBeforePasswordChange(@RequestParam String token) {
        if (!resetPasswordTokenService.isTokenFound(token)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ResetPasswordToken not found, try again later.");
        } else if (resetPasswordTokenService.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ResetPasswordToken expired.");
        } else return ResponseEntity.ok(token);
    }

    @PatchMapping(path = "/change_password")
    public ResponseEntity<?> changePasswordWithResetPasswordToken(
            @RequestParam String token,
            @RequestBody String passedNewPassword) {
        if (!userService.isPasswordValid(passedNewPassword)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    "Password does not fit into required pattern.");
        } else if (!resetPasswordTokenService.isTokenFound(token) && resetPasswordTokenService.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Token don't exists or expired, try reset your password later.");
        } else {
            User user = resetPasswordTokenService.getUserByResetPasswordTokenSignedTo(token);
            userService.changePasswordOfSpecificUser(passedNewPassword, user.getEmailAddress());
            return ResponseEntity.ok().build();
        }
    }
}
