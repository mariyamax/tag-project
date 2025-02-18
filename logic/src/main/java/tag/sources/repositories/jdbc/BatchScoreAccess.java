package tag.sources.repositories.jdbc;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tag.sources.models.tables.BatchScore;

import java.util.*;

import static tag.sources.utils.SqlUtils.DEFAULT_PAGE_SIZE;

@Repository
public class BatchScoreAccess {
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS batch_score_${batchId} ("
                    + "user_id BIGINT,"
                    + "source_id BIGINT,"
                    + "mark json,"
                    + "PRIMARY KEY (user_id, source_id));";

    private static final String SELECT_ALL_WITHOUT_PAGING =
            "SELECT * FROM batch_score_${batchId} "
                    + "ORDER BY source_id";

    private static final String SELECT_ALL =
            "SELECT * FROM batch_score_${batchId} "
                    + "${sourceTemplate} "
                    + "ORDER BY source_id LIMIT ${limit} OFFSET ${offset}";

    private static final String INSERT_SOURCE_MARK =
            "INSERT INTO batch_score_${batchId} (user_id, source_id, mark) "
                    + "${valuesTemplate} "
                    + "ON CONFLICT (user_id, source_id) DO UPDATE "
                    + "SET mark = EXCLUDED.MARK;";


    private static final String DELETE_DATA_SQL =
            "DROP TABLE IF EXISTS batch_score_${batchId} CASCADE;";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public BatchScoreAccess(JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public void createBatchScoreTable(Long batchId) {
        String builtQuery = StringSubstitutor.replace(CREATE_TABLE_SQL, Map.of("batchId", batchId));
        namedParameterJdbcTemplate.update(builtQuery, Map.of());
    }

    public List<BatchScore> selectAllBatchScores(Long batchId, Integer page, Optional<Long> sourceId) {
        String sourceTemplate = sourceId.isPresent()
                ? StringSubstitutor.replace("WHERE source_id = ${sourceId}", Map.of("sourceId", sourceId))
                : "";

        String builtQuery = StringSubstitutor.replace(SELECT_ALL, Map.of(
                "batchId", batchId,
                "sourceTemplate", sourceTemplate,
                "limit", DEFAULT_PAGE_SIZE,
                "offset", page * DEFAULT_PAGE_SIZE));

        return namedParameterJdbcTemplate.query(builtQuery, Map.of(), rs -> {
            List<BatchScore> batchScores = new ArrayList<>();
            while (rs.next()) {
                batchScores.add(new BatchScore(
                        rs.getLong("user_id"),
                        rs.getLong("source_id"),
                        rs.getString("mark")
                ));
            }
            return batchScores;
        });
    }

    public List<BatchScore> selectAllBatchScoresWithoutPaging(Long batchId) {
        String builtQuery = StringSubstitutor.replace(SELECT_ALL_WITHOUT_PAGING, Map.of("batchId", batchId));

        return namedParameterJdbcTemplate.query(builtQuery, Map.of(), rs -> {
            List<BatchScore> batchScores = new ArrayList<>();
            while (rs.next()) {
                batchScores.add(new BatchScore(
                        rs.getLong("user_id"),
                        rs.getLong("source_id"),
                        rs.getString("mark")
                ));
            }
            return batchScores;
        });
    }

    public void insertSourceMark(Long userId, Long batchId, HashMap<Long, String> sourceToMarks) {
        StringBuilder valuesTemplate = new StringBuilder("VALUES ");
        for (Map.Entry<Long, String> entry : sourceToMarks.entrySet()) {
            valuesTemplate.append(" (")
                    .append(userId)
                    .append(", ")
                    .append(entry.getKey())
                    .append(", '")
                    .append(entry.getValue())
                    .append("'),");
        }
        //remove comma
        valuesTemplate.deleteCharAt(valuesTemplate.length() - 1);
        String builtQuery = StringSubstitutor.replace(INSERT_SOURCE_MARK, Map.of(
                "batchId", batchId, "valuesTemplate", valuesTemplate));
        namedParameterJdbcTemplate.update(builtQuery, Map.of());
    }

    public void removeTable(Long batchId) {
        String builtQuery = StringSubstitutor.replace(DELETE_DATA_SQL, Map.of("batchId", batchId));
        namedParameterJdbcTemplate.update(builtQuery, Map.of());
    }
}
