package tag.sources.services;

import org.springframework.stereotype.Service;
import tag.sources.models.Dictionary;
import tag.sources.models.TableDefinition;
import tag.sources.repositories.jpa.DictionaryRepository;

import java.util.Collection;
import java.util.List;

@Service
public class DictionaryService {
    private final DictionaryRepository dictionaryRepository;

    public DictionaryService(DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository = dictionaryRepository;
    }

    public List<Dictionary> findAll(Long page) {
        return dictionaryRepository.findAll();
    }

    public List<Dictionary> findAllByTableDefinition(Long page, String tableDefinition) {
        return dictionaryRepository.findAllByTableDefinition(TableDefinition.valueOf(tableDefinition));
    }
}
