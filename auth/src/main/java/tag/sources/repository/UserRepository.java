package tag.sources.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tag.sources.models.Role;
import tag.sources.models.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    void deleteUserByEmail(String email);

    List<User> findAllByRole(Role role);

    List<User> findAllByRoleIn(Collection<Role> roles);
}