package networksTwo.domain.model;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.util.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Username must be alpha")
    private String username;

    @Column(unique = true, nullable = false)
    @Email(message = "Email should be valid")
    private String email;

    @Column(nullable = false)
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*]+$", message = "Password must be alphanumeric and may include special characters")
    private String password;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private List<Chat> chats = new ArrayList<>();

    @Transactional
    public List<Chat> getChats() {
        return chats;
    }


    public void setUsername(@Pattern(regexp = "^[a-zA-Z]+$", message = "Username must be alpha") String username) {
        this.username = username;
    }

    public void setEmail(@Email(message = "Email should be valid") String email) {
        this.email = email;
    }

    public void setPassword(@Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*]+$", message = "Password must be alphanumeric and may include special characters") String password) {
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }


    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
