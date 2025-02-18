package tag.sources.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tag.sources.models.Permission;
import tag.sources.models.json.UserPersonalResponse;
import tag.sources.models.tables.UserAchievement;
import tag.sources.services.UserService;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/rating")
    public ResponseEntity<LinkedList<UserAchievement>> findAll(@RequestHeader(required = false) Long page,
                                                               @RequestAttribute(name = "permissions") List<String> permissions,
                                                               @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())
                || permissions.contains(Permission.MARK_TYPES_SEARCH.name())
                || permissions.contains(Permission.MARK_TYPES_MATCH.name())) {
            return ResponseEntity.ok(userService.getRating());
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/personal")
    public ResponseEntity<UserPersonalResponse> findAll(@RequestAttribute(name = "permissions") List<String> permissions,
                                                        @RequestAttribute(name = "userId") Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(userService.getPersonalPageData(userId));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
