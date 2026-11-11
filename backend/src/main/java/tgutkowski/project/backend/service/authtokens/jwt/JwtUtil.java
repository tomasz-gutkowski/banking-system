package tgutkowski.project.backend.service.authtokens.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import tgutkowski.project.backend.model.entities.AppUser;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey signingKey;

    @Value("${jwt.expiration.milliseconds}")
    private long expiration;

    @PostConstruct
    public void init() {
        if (this.secret == null || this.secret.length() < 32) {
            throw new IllegalStateException("JWT secret required to be at least 32 characters long, substitute JWT_SECRET in .env with proper value");
        }
        byte[] encodedKey = Base64.getEncoder().encode(secret.getBytes());
        this.signingKey = Keys.hmacShaKeyFor(encodedKey);
    }

    public String generateToken(AppUser appUser) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(appUser.getUsername())
                .claim("roles", appUser.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(signingKey)
                .compact();
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
