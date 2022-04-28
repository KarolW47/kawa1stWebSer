package pl.webser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "role_name")
    private String roleName;

    @JsonIgnore
    @ManyToMany(mappedBy = "userRoles")
    List<User> users = new ArrayList<User>();


    @Override
    public String toString() {
        return "id=" + id +
                ", roleName='" + roleName + '\'';
    }
}
