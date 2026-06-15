package ma.gov.justice.gestion_dossiers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isTemporaryPassword;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String email, String firstName, String lastName, boolean isTemporaryPassword, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isTemporaryPassword = isTemporaryPassword;
        this.roles = roles;
    }
}
