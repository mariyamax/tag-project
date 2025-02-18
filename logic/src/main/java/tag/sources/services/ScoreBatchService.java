package tag.sources.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tag.sources.models.json.ScoreRequest;
import tag.sources.models.tables.BatchScore;
import tag.sources.repositories.jdbc.BatchScoreAccess;
import tag.sources.repositories.jdbc.UserAchievementsAccess;
import tag.sources.repositories.jdbc.UserScoreAccess;

import java.util.HashMap;
import java.util.List;

@Service
public class ScoreBatchService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final UserScoreAccess userScoreAccess;
    private final BatchScoreAccess batchScoreAccess;
    private final UserAchievementsAccess userAchievementsAccess;

    @Autowired
    public ScoreBatchService(UserScoreAccess userScoreAccess,
                             BatchScoreAccess batchScoreAccess,
                             UserAchievementsAccess userAchievementsAccess) {
        this.userScoreAccess = userScoreAccess;
        this.batchScoreAccess = batchScoreAccess;
        this.userAchievementsAccess = userAchievementsAccess;
    }

    @SneakyThrows
    @Transactional
    public void markBatch(Long batchId, Long userId, ScoreRequest scoreRequest) {
        HashMap<Long, String> sourcesToMarksMap = new HashMap<>();
        HashMap<String, String> entitySourcehashMap = new HashMap<>();
        String grade = OBJECT_MAPPER.writeValueAsString(scoreRequest.getGrade());
        String entity = OBJECT_MAPPER.writeValueAsString(scoreRequest.getEntity());
        entitySourcehashMap.put(grade, entity);
        //тут должна быть хэш-мап в реквесте. для этого с фронта надо создать возможность множественной отправки данных
        sourcesToMarksMap.put(scoreRequest.getSourceId(), OBJECT_MAPPER.writeValueAsString(entitySourcehashMap));
        userScoreAccess.insertUserBatchData(userId, batchId, sourcesToMarksMap);
        batchScoreAccess.insertSourceMark(userId, batchId, sourcesToMarksMap);
        userAchievementsAccess.incrementScore(userId, sourcesToMarksMap.size());
    }

    @Transactional
    public void markBatchFinal(Long batchId, HashMap<Long, String> sourcesToMarks) {

    }

    public List<BatchScore> getBatchScore(Long batchId) {
        return batchScoreAccess.selectAllBatchScoresWithoutPaging(batchId);
    }
}
