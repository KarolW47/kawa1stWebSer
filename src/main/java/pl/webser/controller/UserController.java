package pl.webser.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.webser.model.Role;
import pl.webser.model.User;
import pl.webser.security.JWTUtil;
import pl.webser.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static pl.webser.security.filter.CustomAuthorizationFilter.ACCESS_TOKEN_HEADER;
import static pl.webser.security.filter.CustomAuthorizationFilter.REFRESH_TOKEN_HEADER;

// TODO: 14.04.2022 Refactor fetching users with sensitive data,
//                  for example in getAllUsers method we cant send this with passwords.
//                  Also other methods needs to be checked at this point.

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        if (userService.isUsernameTaken(user.getUsername())) {
            return responseAfterUnsuccessfulValidation(
                    "Username already exists.");
        } else if (!userService.isUsernameValid(user.getUsername())) {
            return responseAfterUnsuccessfulValidation(
                    "Username does not fit into required pattern.");
        } else if (userService.isEmailAddressTaken(user.getEmailAddress())) {
            return responseAfterUnsuccessfulValidation(
                    "Account with this email address already exists.");
        } else if (!userService.isEmailAddressValid(user.getEmailAddress())) {
            return responseAfterUnsuccessfulValidation(
                    "Email address does not fit into required pattern.");
        } else if (!userService.isPasswordValid(user.getPassword())) {
            return responseAfterUnsuccessfulValidation(
                    "Password does not fit into required pattern.");
        } else {
            log.info("Successfully added user with email: " + user.getEmailAddress() + " to DB.");
            URI uri =
                    URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/register").toUriString());
            return ResponseEntity.created(uri).body(userService.savePassedUser(user));
        }
    }

    @GetMapping(path = "/profile")
    public ResponseEntity<User> getSpecificUser(@RequestParam String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
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
        if (userService.isUserPasswordEqual(confirmationPassword, emailAddress)) {
            userService.deleteSpecificUser(emailAddress);
            return ResponseEntity.ok().build();
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PatchMapping(path = "/profile/username/change")
    public ResponseEntity<?> changeUsername(@RequestHeader(name = ACCESS_TOKEN_HEADER) String token,
                                            @RequestBody String passedUsername) {
        User user = userService.getUserByEmailAddress(jwtUtil.getEmailAddressFromJwtToken(token));
        if (userService.isUsernameTaken(passedUsername)) {
            return responseAfterUnsuccessfulValidation("Username already exists");
        } else if (!userService.isUsernameValid(passedUsername)) {
            return responseAfterUnsuccessfulValidation("Username does not fit into required pattern.");
        } else {
            userService.changeUsernameOfSpecificUser(passedUsername, user.getUsername(), user.getEmailAddress());
            return ResponseEntity.ok().build();
        }
    }

    @PatchMapping(path = "/profile/password/change")
    public ResponseEntity<?> changePassword(@RequestHeader(name = ACCESS_TOKEN_HEADER) String token,
                                            @RequestParam(name = "newPassword") String passedNewPassword,
                                            @RequestParam(name = "oldPassword") String passedConfirmationPassword) {
        String emailAddress = jwtUtil.getEmailAddressFromJwtToken(token);
        if (userService.isUserPasswordEqual(passedConfirmationPassword, emailAddress)) {
            if (userService.isPasswordValid(passedNewPassword)) {
                userService.changePasswordOfSpecificUser(passedNewPassword, emailAddress);
                return ResponseEntity.ok().build();
            } else return responseAfterUnsuccessfulValidation("Password does not fit into required pattern.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PatchMapping(path = "/profile/about_me_info/change")
    public ResponseEntity<?> changeAboutMeInfo(@RequestHeader(name = ACCESS_TOKEN_HEADER) String token,
                                               @RequestBody String passedAboutMeInfo) {
        String emailAddress = jwtUtil.getEmailAddressFromJwtToken(token);
        userService.changeAboutMeInfoOfSpecificUser(passedAboutMeInfo, emailAddress);
        return ResponseEntity.ok().build();
    }


    public ResponseEntity<String> responseAfterUnsuccessfulValidation(String responseMessage) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(responseMessage);
    }

}
