package pl.webser.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.webser.model.User;
import pl.webser.security.JWTUtil;
import pl.webser.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static pl.webser.security.filter.CustomAuthorizationFilter.ACCESS_TOKEN_HEADER;
import static pl.webser.security.filter.CustomAuthorizationFilter.REFRESH_TOKEN_HEADER;


@RestController
@Slf4j
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, JWTUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
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

    @PostMapping(path = "/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);
        if (refreshToken != null) {
            try {
                String username = jwtUtil.getUserNameFromJwtToken(refreshToken);
                User user = userService.getUserByUsername(username);

                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getUsername(),
                                user.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                response.setHeader(ACCESS_TOKEN_HEADER, jwtUtil.generateJwtToken(authentication));
                response.setHeader(REFRESH_TOKEN_HEADER, jwtUtil.generateJwtRefreshToken(authentication));
            } catch (Exception exception) {
                response.sendError(HttpStatus.FORBIDDEN.value());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setHeader("error", exception.getMessage());
            }
        } else throw new RuntimeException("Refresh Token is missing.");
    }


    public ResponseEntity<String> responseAfterUnsuccessfulValidation(String responseMessage) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(responseMessage);
    }

}
