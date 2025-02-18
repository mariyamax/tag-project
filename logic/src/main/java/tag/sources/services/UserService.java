package tag.sources.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tag.sources.models.Dictionary;
import tag.sources.models.TableDefinition;
import tag.sources.models.json.UserPersonalResponse;
import tag.sources.models.tables.UserAchievement;
import tag.sources.repositories.jdbc.UserAchievementsAccess;
import tag.sources.repositories.jpa.DictionaryRepository;
import tag.sources.repositories.jpa.UserAchievementRepository;

import java.util.LinkedList;
import java.util.List;

@Service
public class UserService {
    private final UserAchievementsAccess userAchievementsAccess;
    private final UserAchievementRepository userAchievementsRepository;
    private final DictionaryRepository dictionaryRepository;

    @Autowired
    public UserService(UserAchievementsAccess userAchievementsAccess, UserAchievementRepository userAchievementsRepository, DictionaryRepository dictionaryRepository) {
        this.userAchievementsAccess = userAchievementsAccess;
        this.userAchievementsRepository = userAchievementsRepository;
        this.dictionaryRepository = dictionaryRepository;
    }

    public LinkedList<UserAchievement> getRating() {
        return userAchievementsAccess.findAll();
    }

    public UserPersonalResponse getPersonalPageData(Long userId) {
        UserAchievement userAchievement = userAchievementsRepository.findById(userId).orElse(null);
        if (userAchievement == null) {
            UserAchievement newUserAchievement = new UserAchievement();
            newUserAchievement.setUserId(userId);
            newUserAchievement.setAccuracy(0.0);
            newUserAchievement.setScoresAmount(0L);
            newUserAchievement.setAccuracyAmount(0L);
            userAchievement = userAchievementsRepository.save(newUserAchievement);
        }
        //Находим все батчи по словарю, как таблицы батч-пользователь
        List<Dictionary> assignedBatches = dictionaryRepository.findAllByTableDefinitionAndFirstParam(TableDefinition.USER_SCORE, String.valueOf(userId));
        List<Long> assignedbatchIds = assignedBatches.stream().map(dict  -> Long.valueOf(dict.getAdditionalParam())).toList();
        UserPersonalResponse userPersonalResponse = new UserPersonalResponse(userAchievement, assignedbatchIds);
        return userPersonalResponse;
    }
}
