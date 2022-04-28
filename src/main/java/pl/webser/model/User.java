package pl.webser.model;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.*;


@Entity
@Getter
@Setter
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

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    private List<Role> userRoles = new ArrayList<Role>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user")
    private List<Post> userPosts;


    public void addUserRole(Role role) {
        this.userRoles.add(role);
        role.getUsers().add(this);
    }

    public void removeUserRole(Role role){
        this.userRoles.remove(role);
        role.getUsers().remove(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", password='" + password + '\'' +
                ", aboutMeInfo='" + aboutMeInfo + '\'' +
                ", userRoles=" + userRoles +
                ", userPosts=" + userPosts +
                '}';
    }
}
