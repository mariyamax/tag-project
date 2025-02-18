package tag.sources.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tag.sources.models.Permission;
import tag.sources.models.json.BatchScoreRequest;
import tag.sources.models.json.ScoreRequest;
import tag.sources.models.tables.BatchScore;
import tag.sources.services.ScoreBatchService;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/score")
public class ScoreBatchController {
    private final ScoreBatchService scoreBatchService;

    public ScoreBatchController(ScoreBatchService scoreBatchService) {
        this.scoreBatchService = scoreBatchService;
    }

    @PostMapping
    public ResponseEntity<Void> scoreSource(@RequestHeader(name = "batchId") Long batchId,
                                            @RequestBody ScoreRequest scoreRequest,
                                            @RequestAttribute(name = "permissions") List<String> permissions,
                                            @RequestAttribute(name = "userId") Long userId) {
        //todo check for mathc search put in header
        boolean validatePermissions = true;
        if (validatePermissions) {
            scoreBatchService.markBatch(batchId, userId, scoreRequest);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/final")
    public ResponseEntity<Void> scoreSourceFinal(@RequestHeader(name = "batchId") Long batchId,
                                                 @RequestBody HashMap<Long, String> sourcesToMarks,
                                                 @RequestAttribute(name = "permissions") List<String> permissions,
                                                 @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            scoreBatchService.markBatchFinal(batchId, sourcesToMarks);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("{batchId}")
    public ResponseEntity<List<BatchScore>> scoreSourceFinal(@PathVariable Long batchId,
                                                             @RequestAttribute(name = "permissions") List<String> permissions,
                                                             @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.BATCH_ALL.name()) || permissions.contains(Permission.BATCH_OWNER.name())) {
            return ResponseEntity.ok(scoreBatchService.getBatchScore(batchId));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
