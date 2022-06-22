package pl.webser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


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

    @Column(unique = true, name = "username")
    private String username;

    @Column(unique = true, name = "email_address")
    private String emailAddress;

    @Column(name = "password")
    private String password;

    @Column(name = "about_me_info")
    private String aboutMeInfo;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private List<Role> userRoles = new ArrayList<Role>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user")
    private List<Post> userPosts;


    public void addUserRole(Role role) {
        this.userRoles.add(role);
        role.getUsers().add(this);
    }

    public void removeUserRole(Role role) {
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
