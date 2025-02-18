package tag.sources.services;

import org.springframework.stereotype.Service;
import tag.sources.models.Role;
import tag.sources.models.User;
import tag.sources.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CryptService cryptService;

    public UserService(UserRepository userRepository, CryptService cryptService) {
        this.userRepository = userRepository;
        this.cryptService = cryptService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(new User());
    }

    public void deleteUser(String email) {
        userRepository.deleteUserByEmail(email);
    }

    public User updateUser(String email, User user) {
        User repositoryUserToUpdate = userRepository.findByEmail(email).orElseThrow();
        repositoryUserToUpdate.setEmail(email);
        String hashedPassword = cryptService.hashPassword(user.getPassword());
        repositoryUserToUpdate.setPassword(hashedPassword);
        repositoryUserToUpdate.setRole(user.getRole());
        userRepository.save(repositoryUserToUpdate);
        return repositoryUserToUpdate;
    }

    public List<User> findAllByPermission(String batchType) {
        if (batchType.equals("MATCHING")) {
            return userRepository.findAllByRoleIn(List.of(Role.ASSESSOR_MATCH, Role.ASSESSOR_MAIN));
        } else if (batchType.equals("SEARCH")) {
            return userRepository.findAllByRoleIn(List.of(Role.ASSESSOR_SEARCH, Role.ASSESSOR_MAIN));
        } else {
            return userRepository.findAllByRoleIn(List.of(Role.ASSESSOR_MAIN));
        }
    }
}
