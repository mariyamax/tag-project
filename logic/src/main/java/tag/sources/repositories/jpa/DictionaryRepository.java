package tag.sources.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tag.sources.models.Dictionary;
import tag.sources.models.TableDefinition;

import java.util.List;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {
    List<Dictionary> findAllByTableDefinitionAndFirstParam(TableDefinition tableDefinition, String firstParam);

    List<Dictionary> findAllByTableDefinition(TableDefinition tableDefinition);

    List<Dictionary> findAllByTableDefinitionAndFirstParamAndAdditionalParam(TableDefinition tableDefinition, String firstParam, String additionalParam);

    List<Dictionary> findAllByTableDefinitionAndAdditionalParam(TableDefinition tableDefinition, String additionalParam);
}
