package scapp.apiauth.interfaces.services;

import scapp.apiauth.entity.usuarios.EUsuario;

import java.util.List;

public interface IJwtService {


    String generateToken(EUsuario usuario, List<String> roles);
    String extractUsername(String token);

    boolean isTokenValid(String token, EUsuario usuario);
}
