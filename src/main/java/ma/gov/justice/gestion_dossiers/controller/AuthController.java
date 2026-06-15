package ma.gov.justice.gestion_dossiers.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ma.gov.justice.gestion_dossiers.dto.ChangePasswordRequest;
import ma.gov.justice.gestion_dossiers.dto.ForgotPasswordRequest;
import ma.gov.justice.gestion_dossiers.dto.JwtResponse;
import ma.gov.justice.gestion_dossiers.dto.LoginRequest;
import ma.gov.justice.gestion_dossiers.security.JwtUtils;
import ma.gov.justice.gestion_dossiers.security.UserDetailsImpl;
import ma.gov.justice.gestion_dossiers.service.SystemLogService;
import ma.gov.justice.gestion_dossiers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user login and token management")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Autowired
    SystemLogService logService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());

        logService.log(userDetails.getUsername(), "connexion", "Système", "Connexion réussie", "login");

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.isTemporaryPassword(),
                roles));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout the current user and log the event")
    public ResponseEntity<?> logoutUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            logService.log(userDetails.getUsername(), "déconnexion", "Système", "Déconnexion", "logout");
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Déconnexion réussie");
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a temporary password via email")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Un mot de passe temporaire a été envoyé à votre adresse email.");
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change the password for the authenticated user")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        userService.changePassword(userDetails.getId(), request.getNewPassword());
        return ResponseEntity.ok("Le mot de passe a été changé avec succès.");
    }
}
