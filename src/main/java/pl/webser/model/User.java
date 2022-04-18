package pl.webser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(unique = true, name = "username")
    @NotEmpty(message = "Username cannot be empty or null")
    private String username;

    @NonNull
    @Column(unique = true, name = "email_address")
    @NotEmpty(message = "Email address cannot be empty or null")
    private String emailAddress;

    @NonNull
    @Column(name = "password")
    @NotEmpty(message = "Password cannot be empty or null")
    private String password;

    @Column(name = "about_me_info")
    private String aboutMeInfo;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> userRoles;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user")
    private List<Post> userPosts;
}
