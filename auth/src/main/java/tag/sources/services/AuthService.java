package tag.sources.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tag.sources.models.Permission;
import tag.sources.models.Role;
import tag.sources.models.User;
import tag.sources.repository.UserRepository;
import tag.sources.services.security.JwtUtils;

import java.util.List;

import static tag.sources.models.Role.*;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final CryptService cryptService;

    @Autowired
    public AuthService(UserRepository userRepository, JwtUtils jwtUtils, CryptService cryptService) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.cryptService = cryptService;
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException(email));

        if (!cryptService.validatePassword(password, user.getPassword())) {
            throw new IllegalStateException(password);
        }

        return jwtUtils.generateToken(email, user.getRole(), user.getId());
    }

    public String registerAssessor(String email, String password, List<String> markTypes) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException(email);
        }
        Role role = markTypes.size() > 1 ? ASSESSOR_MAIN
                : markTypes.get(0).equals(Permission.MARK_TYPES_MATCH.getPermission())
                ? ASSESSOR_MATCH : ASSESSOR_SEARCH;

        User user = createAndSave(email, password, role);

        return jwtUtils.generateToken(email, user.getRole(), user.getId());
    }

    private User createAndSave(String email, String password, Role role) {
        String hashedPassword = cryptService.hashPassword(password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRole(role);
        userRepository.save(user);
        return user;
    }

    public boolean validate(String authorization) {
        return authorization != null && jwtUtils.validate(authorization.replaceAll("Bearer ", ""));
    }
}
