package users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;


}
