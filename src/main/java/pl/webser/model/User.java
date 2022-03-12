package pl.webser.model;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


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

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> userRoles = new ArrayList<>();

    @OneToMany()
    @JoinColumn(name = "user")
    private List<Post> userPosts;
}
