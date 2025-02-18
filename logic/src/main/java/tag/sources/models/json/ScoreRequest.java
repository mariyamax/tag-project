package tag.sources.models.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRequest {
    private List<String> entity;
    private List<String> grade;
    private Long sourceId;
}
