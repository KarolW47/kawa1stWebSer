package pl.webser;

import lombok.extern.slf4j.Slf4j;
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

import static pl.webser.security.SecurityConfig.*;


@SpringBootApplication
public class ServerApp {
    public static void main(String[] args) {
        SpringApplication.run(ServerApp.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner run(UserService userService, RoleService roleService, PostService postService) {
        return args -> {

            Role roleU = new Role();
            Role roleM = new Role();
            Role roleA = new Role();
            roleU.setRoleName(ROLE_USER);
            roleM.setRoleName(ROLE_MODERATOR);
            roleA.setRoleName(ROLE_ADMIN);
            roleService.addRole(roleU);
            roleService.addRole(roleM);
            roleService.addRole(roleA);

            User adminUser = new User();
            adminUser.setUsername("admin1");
            adminUser.setPassword("admin1");
            adminUser.setEmailAddress("admin1@admin.ad");
            userService.savePassedUser(adminUser);

            userService.addRoleToRegisteredUser("admin1@admin.ad", ROLE_MODERATOR);
            userService.addRoleToRegisteredUser("admin1@admin.ad", ROLE_ADMIN);

            postService.addPost("admin1@admin.ad", "Hello Everyone!");


            User exampleUser = new User();
            exampleUser.setUsername("example");
            exampleUser.setPassword("example");
            exampleUser.setEmailAddress("example@example.ex");
            userService.savePassedUser(exampleUser);

            postService.addPost("example@example.ex", "Example message.");

        };
    }
}
