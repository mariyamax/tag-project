package tag.sources.models.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tag.sources.models.tables.UserAchievement;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPersonalResponse {
    private UserAchievement userAchievement;
    private List<Long> assignedBatch;
}
