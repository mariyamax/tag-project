package tag.sources.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tag.sources.models.Permission;
import tag.sources.services.BatchService;

import java.util.List;

@RestController
@RequestMapping("/batch")
public class BatchController {
    private final BatchService batchService;

    @Autowired
    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    //При создании батча надо создать все дополнительные таблицы
    @PostMapping("/create")
    public ResponseEntity<Void> createBatchTables(@RequestHeader(name = "batchId") Long batchId,
                                                  @RequestAttribute(name = "permissions") List<String> permissions,
                                                  @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            batchService.createBatchTables(batchId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    //При назначении батча надо создать таблицы пользователь-батч
    @PostMapping("/assign")
    public ResponseEntity<Void> assignBatch(@RequestHeader(name = "batchId") Long batchId,
                                            @RequestHeader(name = "assessorId") Long assessorId,
                                            @RequestAttribute(name = "permissions") List<String> permissions,
                                            @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            batchService.createUserBatchTables(batchId, assessorId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    //После выставление финальной оценки убираем все
    @GetMapping("/remove")
    public ResponseEntity<Void> remove(@RequestHeader(name = "batchId") Long batchId,
                                       @RequestAttribute(name = "permissions") List<String> permissions,
                                       @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            batchService.removeAllBatchTables(batchId, List.of());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
