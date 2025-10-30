package fr.mecanique.api.pmgl.pmgl_api.security;

import javax.crypto.SecretKey;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey secretKey = Keys.hmacShaKeyFor("super-secure-pmgl-invite-key-256-bit-value".getBytes());

    private final long expirationMs = 24 * 60 * 60 * 1000;

    public String generateInvitationToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateInvitationToken(String token) throws Exception {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new Exception("Token expiré");
        } catch (Exception e) {
            throw new Exception("Token invalide");
        }
    }
}
