package ma.gov.justice.gestion_dossiers.serviceImpl;


import jakarta.transaction.Transactional;
import ma.gov.justice.gestion_dossiers.entity.User;
import ma.gov.justice.gestion_dossiers.repository.UserRepository;
import ma.gov.justice.gestion_dossiers.service.EmailService;
import ma.gov.justice.gestion_dossiers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Override public List<User> getAllUsers() { return userRepository.findAll(); }

    @Override public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override public User createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("USERNAME_EXISTS");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("EMAIL_EXISTS");
        }
        if (userRepository.findByPhone(user.getPhone()).isPresent()) {
            throw new RuntimeException("PHONE_EXISTS");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        systemLogService.log("Admin", "createUser", saved.getUsername(), "إنشاء حساب مستخدم جديد", "create");
        return saved;
    }

    @Override public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.findByUsername(userDetails.getUsername()).ifPresent(u -> {
            if (!u.getUserId().equals(id)) throw new RuntimeException("USERNAME_EXISTS");
        });
        userRepository.findByEmail(userDetails.getEmail()).ifPresent(u -> {
            if (!u.getUserId().equals(id)) throw new RuntimeException("EMAIL_EXISTS");
        });
        userRepository.findByPhone(userDetails.getPhone()).ifPresent(u -> {
            if (!u.getUserId().equals(id)) throw new RuntimeException("PHONE_EXISTS");
        });

        if (userDetails.getUsername() != null) user.setUsername(userDetails.getUsername());
        if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
        if (userDetails.getFirstName() != null) user.setFirstName(userDetails.getFirstName());
        if (userDetails.getLastName() != null) user.setLastName(userDetails.getLastName());
        if (userDetails.getRole() != null) user.setRole(userDetails.getRole());
        if (userDetails.getPhone() != null) user.setPhone(userDetails.getPhone());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        User updated = userRepository.save(user);
        systemLogService.log("Admin", "updateUser", updated.getUsername(), "تعديل بيانات المستخدم", "update");
        return updated;
    }

    @Override public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(u -> {
            systemLogService.log("Admin", "deleteUser", u.getUsername(), "حذف حساب مستخدم", "delete");
            userRepository.deleteById(id);
        });
    }

    @Override public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        String tempPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setTemporaryPassword(true);
        userRepository.save(user);
        
        emailService.sendTemporaryPassword(user.getEmail(), tempPassword);
        systemLogService.log(user.getUsername(), "forgotPassword", user.getUsername(), "Demande de réinitialisation de mot de passe", "update");
    }

    @Override public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTemporaryPassword(false);
        userRepository.save(user);
        systemLogService.log(user.getUsername(), "changePassword", user.getUsername(), "Changement de mot de passe", "update");
    }

    private String generateRandomPassword() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    @Autowired
    private ma.gov.justice.gestion_dossiers.service.SystemLogService systemLogService;
}
