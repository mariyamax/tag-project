package tag.sources.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tag.sources.models.Dictionary;
import tag.sources.models.TableDefinition;
import tag.sources.repositories.jdbc.BatchFinalAccess;
import tag.sources.repositories.jdbc.BatchScoreAccess;
import tag.sources.repositories.jdbc.UserScoreAccess;
import tag.sources.repositories.jpa.DictionaryRepository;

import java.util.List;

@Service
public class BatchService {
    private final BatchFinalAccess batchFinalAccess;
    private final BatchScoreAccess batchScoreAccess;
    private final UserScoreAccess userScoreAccess;
    private final DictionaryRepository dictionaryRepository;

    @Autowired
    public BatchService(BatchFinalAccess batchFinalAccess, BatchScoreAccess batchScoreAccess, UserScoreAccess userScoreAccess, DictionaryRepository dictionaryRepository) {
        this.batchFinalAccess = batchFinalAccess;
        this.batchScoreAccess = batchScoreAccess;
        this.userScoreAccess = userScoreAccess;
        this.dictionaryRepository = dictionaryRepository;
    }

    public void createBatchTables(Long batchId) {
        batchFinalAccess.createBatchScoreTable(batchId);
        batchScoreAccess.createBatchScoreTable(batchId);

        List<Dictionary> batchFinalDictionary = dictionaryRepository
                .findAllByTableDefinitionAndFirstParam(TableDefinition.BATCH_FINAL, String.valueOf(batchId.intValue()));
        if (batchFinalDictionary.isEmpty()) {
            Dictionary dictionary = new Dictionary();
            dictionary.setTableDefinition(TableDefinition.BATCH_FINAL);
            dictionary.setFirstParam(String.valueOf(batchId.intValue()));
            dictionaryRepository.save(dictionary);
        }
        List<Dictionary> batchScoreDictionary = dictionaryRepository
                .findAllByTableDefinitionAndFirstParam(TableDefinition.BATCH_SCORE, String.valueOf(batchId.intValue()));
        if (batchScoreDictionary.isEmpty()) {
            Dictionary dictionary = new Dictionary();
            dictionary.setTableDefinition(TableDefinition.BATCH_SCORE);
            dictionary.setFirstParam(String.valueOf(batchId.intValue()));
            dictionaryRepository.save(dictionary);
        }
    }

    public void createUserBatchTables(Long batchId, Long assessorId) {
        userScoreAccess.createUserBatch(assessorId, batchId);
        List<Dictionary> userBatchDictionary = dictionaryRepository
                .findAllByTableDefinitionAndFirstParamAndAdditionalParam(TableDefinition.USER_SCORE, String.valueOf(assessorId.intValue()), String.valueOf(batchId.intValue()));
        List<Dictionary> assignedUsersSize = dictionaryRepository
                .findAllByTableDefinitionAndAdditionalParam(TableDefinition.USER_SCORE, String.valueOf(assessorId.intValue()));
        //todo create method to controle assigned users amount.
        if (userBatchDictionary.isEmpty()) {
            Dictionary dictionary = new Dictionary();
            dictionary.setTableDefinition(TableDefinition.USER_SCORE);
            dictionary.setFirstParam(String.valueOf(assessorId.intValue()));
            dictionary.setAdditionalParam(String.valueOf(batchId.intValue()));
            dictionaryRepository.save(dictionary);
        }
    }

    public void removeAllBatchTables(Long batchId, List<Long> userIds) {
        for (Long userId : userIds) {
            userScoreAccess.removeTable(batchId, userId);
        }
        batchScoreAccess.removeTable(batchId);
    }
}
