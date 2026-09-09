package tgutkowski.project.backend.model.entities;

import jakarta.persistence.*;
import tgutkowski.project.backend.model.entities.enums.Roles;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Roles role;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Roles getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Roles role) {
        this.role = role;
    }
}
