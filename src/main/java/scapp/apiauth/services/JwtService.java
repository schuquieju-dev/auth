package scapp.apiauth.services;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import scapp.apiauth.entity.usuarios.EUsuario;
import scapp.apiauth.interfaces.services.IJwtService;

import scapp.apiauth.config.segurity.JwtProperties;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService implements IJwtService {

    private final JwtProperties jwtProperties;

    @Override
    public String generateToken(EUsuario usuario, List<String> roles) {
        Map<String, Object> extraClaims = new HashMap<>();

        // CORRECCIÓN: Cambiar .add por .put
        extraClaims.put("usuarioId", usuario.getId());
        extraClaims.put("personaId", usuario.getPersonaId());
        extraClaims.put("estado", usuario.getEstado().name());
        extraClaims.put("roles", roles);

        // Delegamos la construcción al método centralizado buildToken
        return buildToken(extraClaims, usuario.getCorreo());
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

    // Unificamos la construcción usando la sintaxis moderna de jjwt v0.12.x
    private String buildToken(Map<String, Object> extraClaims, String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey()) // Ya no requiere pasar el SignatureAlgorithm explícito en versiones nuevas
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
                .getPayload(); // Sintaxis moderna v0.12.x+
    }

    private SecretKey getSigningKey() {
        // Nota: Si tu llave en properties está en texto plano, usa .getBytes().
        // Si está codificada en Base64, recuerda usar: Decoders.BASE64.decode(jwtProperties.getSecret())
        byte[] keyBytes = jwtProperties.getSecret().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}