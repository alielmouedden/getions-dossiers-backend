package ma.gov.justice.gestion_dossiers.service;

import ma.gov.justice.gestion_dossiers.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    Optional<User> getUserByUsername(String username);
    void forgotPassword(String email);
    void changePassword(Long userId, String newPassword);
}