package ma.gov.justice.gestion_dossiers.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private boolean isTemporaryPassword = false;

    // User 1..* Folder
    @OneToMany(mappedBy = "createdBy", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Folder> folders;
}

