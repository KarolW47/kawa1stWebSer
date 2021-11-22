package users;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;

@Entity
@Table(name = "user")
@AllArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    private String username;

    @NonNull
    private String password;
}
