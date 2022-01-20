package pl.webser.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.webser.model.User;
import pl.webser.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@Slf4j
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //method to check list of users, just for tests
    @GetMapping(path = "/users")
    public ResponseEntity getUsers() {
        List<User> usersFromDb = userRepository.findAll();
        return ResponseEntity.ok(usersFromDb);
    }

    //register new users method
    @PostMapping(path = "/register")
    public ResponseEntity addUser(@RequestBody User user) {
        //validate if username already exists && fits in required length && fits in expected regex;
        Boolean isUserExists = userRepository.existsByUsername(user.getUsername());
        String providedUsername = user.getUsername();
        String usernameRegex = "^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";

        if (isUserExists
                || providedUsername.length() < 6
                || providedUsername.length() > 24
                || !(Pattern.matches(usernameRegex, providedUsername))) {
            return responseAfterUnsuccessfulValidation("Username does not fit into required pattern or already exists.");
        }

        //validate if email address already exists && fits in expected regex
        Boolean isEmailAddressExists = userRepository.existsByEmailAddress(user.getEmailAddress());
        String providedEmailAddress = user.getEmailAddress();
        String emailAddressRegex = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}$";

        if (isEmailAddressExists
                || !(Pattern.matches(emailAddressRegex, providedEmailAddress))) {
            return responseAfterUnsuccessfulValidation("Email address does not fit into required pattern or already exists.");
        }

        //validate if password fits in required length
        String providedPassword = user.getPassword();
        if(providedPassword.length() < 6 || providedPassword.length() > 35) {
            return responseAfterUnsuccessfulValidation("Password does not fit into required pattern.");
        }

        User userToSave = userRepository.save(user);
        log.info("Successfully added user: " + userToSave.getUsername() + " to DB.");
        return ResponseEntity.ok(user);
    }

    public ResponseEntity responseAfterUnsuccessfulValidation(String responseMessage) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(responseMessage);
    }

}
