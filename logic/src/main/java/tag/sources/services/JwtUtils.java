package tag.sources.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    private static String SECRET;
    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        SECRET = secret;
    }

    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractEmail(String token) {
        return extractClaims(token).getSubject();
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

    public static boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}

