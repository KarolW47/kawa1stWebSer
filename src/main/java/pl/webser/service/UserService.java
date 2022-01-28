package pl.webser.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.webser.model.Role;
import pl.webser.model.User;
import pl.webser.repository.RoleRepository;
import pl.webser.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepository.findByUsername(username);
       if(user == null){
           throw new UsernameNotFoundException("User not found in database!");
       } else {
           log.info("User found in database.");
           Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
           user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
           return new org.springframework.security.core.userdetails.User(
                   user.getUsername(), user.getPassword(), authorities);
       }
    }

    public List<User> getUsers() {
        log.info("Fetching all users from database.");
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        log.info("Saving new user ({}) to database.", user.getUsername());
        return userRepository.save(user);
    }

    public Boolean isUsernameValid(String username) {
        String usernameRegex = "^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
        log.info("Validating username with required length and pattern");
        return username.length() < 6
                || username.length() > 24
                || Pattern.matches(usernameRegex, username);
    }

    public Boolean isUsernameTaken(String username) {
        log.info("Validating if username is already taken.");
        return userRepository.existsByUsername(username);
    }

    public Boolean isEmailAddressValid(String emailAddress) {
        String emailAddressRegex = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}$";
        log.info("Validating emailAddress with required pattern");
        return Pattern.matches(emailAddressRegex, emailAddress);
    }

    public Boolean isEmailAddressTaken(String emailAddress) {
        log.info("Validating if emailAddress is already taken.");
        return userRepository.existsByEmailAddress(emailAddress);
    }

    public Boolean isPasswordValid(String password) {
        log.info("Validating password with required length.");
        return password.length() < 6 || password.length() > 35;
    }

    public Role saveRole(Role role) {
        log.info("Saving new role ({}) to database.", role.getName());
        return roleRepository.save(role);
    }

    public Boolean isRoleAlreadyExists(String name) {
        log.info("Validating if role ({}) already exists  to database.", name);
        return roleRepository.existsByName(name);
    }

    public void addRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);
        log.info("Saving role ({}) to user ({}) into database.", role.getName(), user.getUsername());
        user.getRoles().add(role);
    }


}
