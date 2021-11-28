package pl.webser.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    //method for tests
    @GetMapping(path = "/users")
    public ResponseEntity getUsers () throws JsonProcessingException {
        List<User> usersFromDb = userRepository.findAll();
        return ResponseEntity.ok(objectMapper.writeValueAsString(usersFromDb));
    }

    //register new users method
    @PostMapping(path = "/register")
    public ResponseEntity addUser(@RequestBody User user){
        Optional<User> userFromDb = userRepository.findByUserName(user.getUsername());
        if (userFromDb.isPresent()){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }
}
