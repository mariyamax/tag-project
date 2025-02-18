package tag.sources.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dictionary")
@Getter
@Setter
@NoArgsConstructor
public class Dictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "table_definition")
    @Enumerated(value = EnumType.STRING)
    private TableDefinition tableDefinition;
    @Column(name = "first_param")
    private String firstParam;
    @Column(name = "additional_param")
    private String additionalParam;
}
