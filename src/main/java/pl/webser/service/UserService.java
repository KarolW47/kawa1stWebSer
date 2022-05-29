package pl.webser.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.webser.model.Role;
import pl.webser.model.User;
import pl.webser.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (isUsernameTaken(username)) {
            User userFromDb = getUserByUsername(username);
            log.info("User found in database: {}", username);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            userFromDb.getUserRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRoleName())));
            return new org.springframework.security.core.userdetails.User(userFromDb.getUsername(),
                    userFromDb.getPassword(),
                    authorities);
        } else throw new UsernameNotFoundException("User not found in DB.");
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        log.info("Fetching all users from database.");
        return userRepository.findAll();
    }

    public User savePassedUser(User user) {
        log.info("Saving new user ({}) to database.", user.getUsername());
        user.setPassword(encodePassword(user.getPassword()));
        user.addUserRole(roleService.getRoleByRoleName("ROLE_USER"));
        return userRepository.save(user);
    }

    public Boolean isUsernameValid(String username) {
        String usernameRegex = "^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
        log.info("Validating username with required length and pattern");
        return username.length() >= 6
                && username.length() <= 24
                && Pattern.matches(usernameRegex, username);
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
        return password.length() >= 6 && password.length() <= 35;
    }

    public String encodePassword(String password) {
        return passwordEncoder().encode(password);
    }


    public void addRoleToRegisteredUser(String username, String roleName) {
        User user = getUserByUsername(username);
        Role role = roleService.getRoleByRoleName(roleName);
        log.info("Saving role ({}) to user ({}) into database.", role.getRoleName(), user.getUsername());
        user.addUserRole(role);
        userRepository.updateUserWithNewRoleList(user, user.getId());
    }

    public boolean isUserPasswordEqual(String passedPassword, String username) {
        String passwordFromDb = userRepository.findByUsername(username).getPassword();
        log.info("Checking if passed password is equal to actual password from DB for user: {}", username);
        return passwordEncoder().matches(passedPassword, passwordFromDb);
    }

    public void deleteSpecificUser(String username) {
        log.info("Deleting user: {}", username);
        userRepository.deleteByUsername(username);
    }

    public void changeUsernameOfSpecificUser(String passedUsername, String oldUsername) {
        log.info("Updating username of user: {}, with new value: {}", oldUsername, passedUsername);
        userRepository.updateUserUsernameByUsername(passedUsername, oldUsername);
    }

    public void changePasswordOfSpecificUser(String passedPassword, String username) {
        log.info("Updating password of user: {}", username);
        userRepository.updateUserPasswordByUsername(passedPassword, username);
    }

    public void changeAboutMeInfoOfSpecificUser(String passedAboutMeInfo, String username) {
        log.info("Updating aboutMeInfo of a user: {}", username);
        userRepository.updateUserAboutMeInfoByUsername(passedAboutMeInfo, username);
    }

}
