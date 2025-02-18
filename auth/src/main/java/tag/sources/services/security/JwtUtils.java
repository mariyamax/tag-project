package tag.sources.services.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tag.sources.models.Role;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    private static String SECRET;
    private static long EXPIRATION;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expired.millis}")
    private Long expirationMillis;

    @PostConstruct
    public void init() {
        SECRET = secret;
        EXPIRATION = expirationMillis;
    }

    public static String generateToken(String mail, Role role, Long id) {
        return Jwts.builder()
                .setSubject(mail)
                .claim("permissions", role.getPermissions())
                .claim("role", role)
                .claim("id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public static Role extractRole(String token) {
        return Role.valueOf(extractClaims(token).get("role", String.class));
    }

    public static List<String> extractPermissions(String token) {
        return (List<String>) extractClaims(token).get("permissions");
    }

    public static Long extractId(String token) {
        return extractClaims(token).get("id", Long.class);
    }

    public static boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public static boolean validate(String token) {
        return !isTokenExpired(token);
    }
}
