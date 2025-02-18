package tag.sources.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tag.sources.models.Permission;
import tag.sources.models.User;
import tag.sources.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{batchType}")
    public ResponseEntity<List<User>> findAllWithPermissions(@PathVariable String batchType,
                                              @RequestAttribute(name = "permissions") List<String> permissions,
                                              @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            return ResponseEntity.ok(userService.findAllByPermission(batchType));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> findAll(@RequestAttribute(name = "permissions") List<String> permissions,
                                              @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            return ResponseEntity.ok(userService.findAll());
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/single")
    public ResponseEntity<User> findByUsername(@RequestBody String email,
                                               @RequestAttribute(name = "permissions") List<String> permissions,
                                               @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            return ResponseEntity.ok(userService.findByEmail(email));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PutMapping
    public ResponseEntity<User> findByUsername(@RequestBody User user,
                                               @RequestAttribute(name = "permissions") List<String> permissions,
                                               @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            return ResponseEntity.ok(userService.updateUser(user.getEmail(), user));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteByUsername(@RequestBody String email,
                                                 @RequestAttribute(name = "permissions") List<String> permissions,
                                                 @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            userService.deleteUser(email);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
