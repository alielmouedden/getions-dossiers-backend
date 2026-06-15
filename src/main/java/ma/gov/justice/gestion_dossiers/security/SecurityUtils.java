package ma.gov.justice.gestion_dossiers.security;

import ma.gov.justice.gestion_dossiers.entity.User;
import ma.gov.justice.gestion_dossiers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        ma.gov.justice.gestion_dossiers.security.UserDetailsImpl userDetails = (ma.gov.justice.gestion_dossiers.security.UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId());
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "System";
        }
        return authentication.getName();
    }
}
