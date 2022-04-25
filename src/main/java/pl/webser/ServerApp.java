package pl.webser;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.webser.model.Role;
import pl.webser.model.User;
import pl.webser.service.PostService;
import pl.webser.service.RoleService;
import pl.webser.service.UserService;

import javax.transaction.Transactional;

@SpringBootApplication
public class ServerApp {
    public static void main(String[] args) {
        SpringApplication.run(ServerApp.class, args);
    }

//    @Bean
//    @Transactional
//    CommandLineRunner run(UserService userService, RoleService roleService, PostService postService) {
//        return args -> {
//            roleService.addRole(new Role(null,"ROLE_USER", null));
//            roleService.addRole(new Role(null,"ROLE_MODERATOR", null));
//            roleService.addRole(new Role(null, "ROLE_ADMIN", null));
//
//            userService.savePassedUser(new User(null,"admin1", "admin1", "admin1",null, null,null));
//
//            userService.addRoleToRegisteredUser("admin1", "ROLE_ADMIN");
//            userService.addRoleToRegisteredUser("admin1", "ROLE_MODERATOR");
//
//
//            postService.addPost("admin1", "Hello Everyone!");
//        };
//    }
}
