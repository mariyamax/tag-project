package tag.sources.repositories.jdbc;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tag.sources.models.tables.UserScore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tag.sources.utils.SqlUtils.DEFAULT_PAGE_SIZE;

@Repository
public class UserScoreAccess {
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS user_score_${userId}_${batchId} ("
                    + "source_id BIGINT,"
                    + "mark json,"
                    + "correct boolean,"
                    + "PRIMARY KEY (source_id))";

    private static final String SELECT_BY_USER_AND_BATCH =
            "SELECT * FROM user_score_${userId}_${batchId} " +
                    "ORDER BY source_id LIMIT ${limit} OFFSET ${offset}";

    //todo increment user marks amount
    private static final String INSERT_DATA_SQL =
            "INSERT INTO user_score_${userId}_${batchId} (source_id, mark) " +
                    " ${valuesTemplate} ON CONFLICT(source_id) DO UPDATE SET mark = EXCLUDED.mark;";

    private static final String DELETE_DATA_SQL =
            "DROP TABLE IF EXISTS user_score_${userId}_${batchId} CASCADE;";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UserScoreAccess(JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public void createUserBatch(Long userId, Long batchId) {
        String builtQuery = StringSubstitutor.replace(CREATE_TABLE_SQL, Map.of("userId", userId, "batchId", batchId));
        namedParameterJdbcTemplate.update(builtQuery, Map.of());
    }

    //todo front control how match send per request
    public void insertUserBatchData(Long userId, Long batchId, HashMap<Long, String> sourceToMarks) {
        StringBuilder valuesTemplate = new StringBuilder("VALUES ");
        for (Map.Entry<Long, String> entry : sourceToMarks.entrySet()) {
            valuesTemplate.append(" (")
                    .append(entry.getKey())
                    .append(", '")
                    .append(entry.getValue())
                    .append("'),");
        }
        //remove comma
        valuesTemplate.deleteCharAt(valuesTemplate.length() - 1);
        String builtQuery = StringSubstitutor.replace(INSERT_DATA_SQL, Map.of(
                "userId", userId, "batchId", batchId, "valuesTemplate", valuesTemplate));
        namedParameterJdbcTemplate.update(builtQuery, Map.of());
    }

    public List<UserScore> getUserBatchData(Long userId, Long batchId, Long page) {
        String builtQuery = StringSubstitutor.replace(SELECT_BY_USER_AND_BATCH, Map.of(
                "userId", userId,
                "batchId", batchId,
                "limit", DEFAULT_PAGE_SIZE,
                "offset", page * DEFAULT_PAGE_SIZE));
        return namedParameterJdbcTemplate.query(builtQuery,Map.of(),  rs -> {
            List<UserScore> userScores = new ArrayList<>();
            while (rs.next()) {
                userScores.add(new UserScore(rs.getLong("source_id"), rs.getString("mark")));
            }
            return userScores;
        });
    }

    public void removeTable(Long batchId, Long userId) {
        String builtQuery = StringSubstitutor.replace(DELETE_DATA_SQL, Map.of("userId", userId, "batchId", batchId));
        namedParameterJdbcTemplate.update(builtQuery, Map.of());
    }
}
