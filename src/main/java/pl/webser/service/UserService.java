package pl.webser.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.webser.model.User;
import pl.webser.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    //method for tests
    @GetMapping(path = "/users")
    public ResponseEntity getUsers () throws JsonProcessingException {
        List<User> usersFromDb = userRepository.findAll();
        return ResponseEntity.ok(objectMapper.writeValueAsString(usersFromDb));
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
