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

@RestController
@Slf4j
@RequestMapping ("/user")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //method to check list of users, just for tests
    @GetMapping(path = "/users")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity getUsers () {
        List<User> usersFromDb = userRepository.findAll();
        return ResponseEntity.ok(usersFromDb);
    }

    //register new users method
    @PostMapping(path = "/register")
    public ResponseEntity addUser(@RequestBody User user){
        log.info("Saving user: " + user.getUsername() + " to DB");
        Optional<User> userFromDb = userRepository.findByUsername(user.getUsername());
        if (userFromDb.isPresent()){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        User savedUser = userRepository.save(user);
        log.info("Successfully added user: " + savedUser.getUsername() + " to DB.");
        return ResponseEntity.ok(savedUser);
    }
}
