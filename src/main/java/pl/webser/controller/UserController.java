package pl.webser.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
@CrossOrigin(origins = "http://localhost:4200")
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
        return ResponseEntity.ok().body(userService.getUsers());
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
            log.info("Successfully added user: " + user.getUsername() + " to DB.");
            URI uri =
                    URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/register").toUriString());
            return ResponseEntity.created(uri).body(userService.saveUser(user));
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
                String username = jwtUtil.getUserNameFromJwtToken(refreshToken);
                log.info("Refreshing token for user: {}, in progress.", username);
                User user = userService.getUserByUsername(username);
                List<String> roles = user.getUserRoles()
                        .stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toList());

                response.setHeader(ACCESS_TOKEN_HEADER, jwtUtil.generateJwtToken(user.getUsername(), roles));
                response.setHeader(REFRESH_TOKEN_HEADER, jwtUtil.generateJwtRefreshToken(user.getUsername()));
                log.info("Refreshing token for user: {}, done.", username);
            } catch (Exception exception) {
                log.info("Error in refreshing token for user: {}, occurred.",
                        jwtUtil.getUserNameFromJwtToken(refreshToken));
                response.sendError(HttpStatus.FORBIDDEN.value());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setHeader("error", exception.getMessage());
                log.info("Exception message: {}.", exception.getMessage());
            }
        } else throw new RuntimeException("Refresh Token is missing.");
    }


    public ResponseEntity<String> responseAfterUnsuccessfulValidation(String responseMessage) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(responseMessage);
    }

}
