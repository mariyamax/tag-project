package tag.sources.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import tag.sources.models.tables.UserAchievement;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
}
