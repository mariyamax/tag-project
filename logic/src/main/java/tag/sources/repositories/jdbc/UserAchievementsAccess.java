package tag.sources.repositories.jdbc;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tag.sources.models.tables.UserAchievement;

import java.util.*;

@Repository
public class UserAchievementsAccess {
    private static final String INCREMENT_SCORE =
            "UPDATE user_achievements "
                    + "SET scores_amount = scores_amount + ${scoreAmount} "
                    + "WHERE user_id = ${userId} ";

    private static final String CALCULATE_ACCURACY =
            "UPDATE user_achievements "
                    + "SET accuracy = (accuracy * accuracy_amount + :accuracy) / (accuracy_amount + 1), "
                    + "accuracy_amount = accuracy_amount + 1 "
                    + "WHERE user_id = ${userId} ";

    private static final String SELECT_ALL =
            "SELECT * FROM user_achievements ORDER BY accuracy DESC, scores_amount DESC;";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UserAchievementsAccess(JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public void incrementScore(Long userId, int scoreAmount) {
        String builtQuery = StringSubstitutor.replace(INCREMENT_SCORE, Map.of("userId", userId, "scoreAmount", scoreAmount));
        namedParameterJdbcTemplate.update(builtQuery, Map.of());
    }

    public void calculateAccuracy(Long userId, Long accuracy) {
        String builtQuery = StringSubstitutor.replace(CALCULATE_ACCURACY, Map.of("userId", userId));
        namedParameterJdbcTemplate.update(builtQuery, Map.of("accuracy", accuracy));
    }

    public LinkedList<UserAchievement> findAll() {
        return namedParameterJdbcTemplate.query(SELECT_ALL, Map.of(), rs -> {
            LinkedList<UserAchievement> userAchievements = new LinkedList<>();
            while (rs.next()) {
                userAchievements.add(new UserAchievement(
                        rs.getLong("user_id"),
                        rs.getLong("scores_amount"),
                        rs.getDouble("accuracy"),
                        rs.getLong("accuracy_amount")
                ));
            }
            return userAchievements;
        });
    }
}
