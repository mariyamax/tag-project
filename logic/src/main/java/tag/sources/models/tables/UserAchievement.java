package tag.sources.models.tables;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_achievements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievement {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "scores_amount")
    private Long scoresAmount;
    @Column(name = "accuracy")
    private Double accuracy;
    @Column(name = "accuracy_amount")
    private Long accuracyAmount;
}
