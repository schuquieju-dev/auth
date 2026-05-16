package scapp.apiauth.services;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import scapp.apiauth.entity.usuarios.EUsuario;
import scapp.apiauth.interfaces.repository.IUsuarioRepository;
import scapp.apiauth.interfaces.services.IUsuarioDetailsService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements IUsuarioDetailsService {

        private final IUsuarioRepository usuarioRepository;


    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        EUsuario usuario = usuarioRepository.findByCorreo(correo.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 1. Buscar los códigos de los roles asociados a la persona del usuario
        List<String> codigosRoles = usuarioRepository.findRolesByCorreo(usuario.getCorreo());

        // 2. Mapearlos a SimpleGrantedAuthority con el prefijo de Spring Security
        List<SimpleGrantedAuthority> authorities = codigosRoles.stream()
                .map(codigo -> new SimpleGrantedAuthority("ROLE_" + codigo.toUpperCase()))
                .collect(Collectors.toList());

        return new User(
                usuario.getCorreo(),
                usuario.getPassword(),
                !Boolean.TRUE.equals(usuario.getBloqueado()),
                true,
                true,
                true,
                authorities // <-- Ahora el contexto de Spring Security conoce sus roles
        );
    }
}
