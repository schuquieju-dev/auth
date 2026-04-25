package scapp.apiauth.interfaces.services;

import scapp.apiauth.entity.usuarios.EUsuario;

public interface IJwtService {
    String generateToken(EUsuario usuario);

    String extractUsername(String token);

    boolean isTokenValid(String token, EUsuario usuario);
}
