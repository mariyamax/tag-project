package tag.sources.repositories.jdbc;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tag.sources.models.tables.BatchFinal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tag.sources.utils.SqlUtils.DEFAULT_PAGE_SIZE;

@Repository
public class BatchFinalAccess {
    //todo можно добавить задачу по расписанию,
    //которая чистит все таблицы для завершенных батчей
    private static final String CREATE_FINAL_TABLE =
            "CREATE TABLE IF NOT EXISTS batch_final_${batchId} ( "
                    + "source_id BIGINT, "
                    + "mark json, "
                    + "PRIMARY KEY (source_id));";

    private static final String SELECT_FROM_FINAL_TABLE =
            "SELECT * FROM batch_final_${batchId} "
                    + "ORDER BY source_id LIMIT ${limit} OFFSET ${offset}";

    private static final String INSERT_FINAL_MARK =
            "INSERT INTO batch_final_${batchId} "
                    + "${valuesTemplate} "
                    + "ON ON CONFLICT (source_id) DO UPDATE "
                    + "SET mark = EXCLUDED.MARK;";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public BatchFinalAccess(JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public void createBatchScoreTable(Long batchId) {
        String builtQuery = StringSubstitutor.replace(CREATE_FINAL_TABLE, Map.of("batchId", batchId));
        namedParameterJdbcTemplate.update(builtQuery, Map.of());
    }

    public List<BatchFinal> selectAllBatchFinal(Long batchId, Integer page) {
        String builtQuery = StringSubstitutor.replace(SELECT_FROM_FINAL_TABLE, Map.of(
                "batchId", batchId,
                "limit", DEFAULT_PAGE_SIZE,
                "offset", page * DEFAULT_PAGE_SIZE));

        return namedParameterJdbcTemplate.query(builtQuery, Map.of(), rs -> {
            List<BatchFinal> batchScores = new ArrayList<>();
            while (rs.next()) {
                batchScores.add(new BatchFinal(
                        rs.getLong("source_id"),
                        rs.getString("mark")
                ));
            }
            return batchScores;
        });
    }

    public void insertSourceMark(Long batchId, HashMap<Long, String> sourceToMarks) {
        StringBuilder valuesTemplate = new StringBuilder("VALUES ");
        for (Map.Entry<Long, String> entry : sourceToMarks.entrySet()) {
            valuesTemplate.append(" (")
                    .append(entry.getKey())
                    .append(", ")
                    .append(entry.getValue())
                    .append("),");
        }
        //remove comma
        valuesTemplate.deleteCharAt(valuesTemplate.length() - 1);
        String builtQuery = StringSubstitutor.replace(INSERT_FINAL_MARK, Map.of(
                "batchId", batchId, "valuesTemplate", valuesTemplate));
        namedParameterJdbcTemplate.update(builtQuery, Map.of());
    }
}
