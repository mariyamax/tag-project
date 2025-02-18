package tag.sources.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tag.sources.models.Dictionary;
import tag.sources.models.Permission;
import tag.sources.services.DictionaryService;

import java.util.List;

@RestController
@RequestMapping("/settings")
public class DictionaryController {
    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Dictionary>> findAll(@RequestHeader(required = false) Long page,
                                                    @RequestAttribute(name = "permissions") List<String> permissions,
                                                    @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            return ResponseEntity.ok(dictionaryService.findAll(page));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/target")
    public ResponseEntity<List<Dictionary>> findAll(@RequestHeader(required = false) Long page,
                                                    @RequestHeader(required = false) String tableDefinition,
                                                    @RequestAttribute(name = "permissions") List<String> permissions,
                                                    @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            return ResponseEntity.ok(dictionaryService.findAll(page));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    //todo ReturnsTable by targetId
    /*@GetMapping("/target/id")
    public ResponseEntity<List<Dictionary>> findAll(@RequestHeader Long targetTableId,
                                                    @RequestAttribute(name = "permissions") List<String> permissions,
                                                    @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            return ResponseEntity.ok(dictionaryService.findAll(page));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }*/

}
