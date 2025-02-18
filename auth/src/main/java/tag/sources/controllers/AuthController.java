package tag.sources.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tag.sources.models.AuthRequest;
import tag.sources.models.AuthResponse;
import tag.sources.models.Permission;
import tag.sources.models.Role;
import tag.sources.services.AuthService;
import tag.sources.services.security.JwtUtils;
import java.util.List;

@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerAssessor(@RequestBody AuthRequest request,
                                                 @RequestAttribute(name = "permissions") List<String> permissions,
                                                 @RequestAttribute(name = "userId") Long userId) {
        if (permissions.contains(Permission.USERS_EDIT.name())) {
            authService.registerAssessor(request.getEmail(), request.getPassword(), request.getMarkTypes());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginAdmin(@RequestBody AuthRequest request) {
        try {
            String token = authService.login(request.getEmail(), request.getPassword());
            Role role = JwtUtils.extractRole(token);
            return ResponseEntity.ok().body(new AuthResponse(token, role));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestHeader(name = "Authorization", required = false) String authorization) {
        if (authService.validate(authorization)) {
            String token = authorization.replaceAll("Bearer ", "");
            Role role = JwtUtils.extractRole(token);
            return ResponseEntity.ok().body(new AuthResponse(token, role));
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}