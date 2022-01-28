package pl.webser.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.webser.model.Role;
import pl.webser.model.User;
import pl.webser.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    @GetMapping(path = "/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping(path = "/createRole")
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        if (userService.isRoleAlreadyExists(role.getName())) {
            return responseAfterUnsuccessfulValidation("Role already exists.");
        } else {
            URI uri =
                    URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/createRole").toUriString());
            return ResponseEntity.created(uri).body(userService.saveRole(role));
        }
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        if (userService.isUsernameTaken(user.getUsername())) {
            return responseAfterUnsuccessfulValidation(
                    "Username already exists.");
        } else if (userService.isUsernameValid(user.getUsername())) {
            return responseAfterUnsuccessfulValidation(
                    "Username does not fit into required pattern.");
        } else if (userService.isEmailAddressTaken(user.getEmailAddress())) {
            return responseAfterUnsuccessfulValidation(
                    "Account with this email address already exists.");
        } else if (userService.isEmailAddressValid(user.getEmailAddress())) {
            return responseAfterUnsuccessfulValidation(
                    "Email address does not fit into required pattern.");
        } else if (userService.isPasswordValid(user.getPassword())) {
            return responseAfterUnsuccessfulValidation(
                    "Password does not fit into required pattern.");
        } else {
            log.info("Successfully added user: " + user.getUsername() + " to DB.");
            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/register").toUriString());
            return ResponseEntity.created(uri).body(userService.saveUser(user));
        }
    }

    public ResponseEntity<String> responseAfterUnsuccessfulValidation(String responseMessage) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(responseMessage);
    }

}
