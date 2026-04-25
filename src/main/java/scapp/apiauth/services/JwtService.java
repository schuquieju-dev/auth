package scapp.apiauth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import scapp.apiauth.config.segurity.JwtProperties;
import scapp.apiauth.entity.usuarios.EUsuario;
import scapp.apiauth.interfaces.services.IJwtService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class JwtService implements  IJwtService {




    private final JwtProperties jwtProperties;

    @Override
    public String generateToken(EUsuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("usuarioId", usuario.getId());
        claims.put("personaId", usuario.getPersonaId());
        claims.put("estado", usuario.getEstado().name());

        return buildToken(claims, usuario.getCorreo());
    }

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public boolean isTokenValid(String token, EUsuario usuario) {
        String username = extractUsername(token);
        return username.equals(usuario.getCorreo()) && !isTokenExpired(token);
    }

    private String buildToken(Map<String, Object> extraClaims, String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}