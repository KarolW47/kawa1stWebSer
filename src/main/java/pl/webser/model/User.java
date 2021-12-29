package pl.webser.model;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @NonNull
    @Column(unique = true)
    @NotEmpty(message = "Username cannot be empty or null")
    private String username;

    @NonNull
    @NotEmpty(message = "Password cannot be empty or null")
    private String password;
}
