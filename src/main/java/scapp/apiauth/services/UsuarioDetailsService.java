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


@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements IUsuarioDetailsService {

        private final IUsuarioRepository usuarioRepository;

        @Override
        public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
            EUsuario usuario = usuarioRepository.findByCorreo(correo.toLowerCase().trim())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            return new User(
                    usuario.getCorreo(),
                    usuario.getPassword(),
                    !Boolean.TRUE.equals(usuario.getBloqueado()),
                    true,
                    true,
                    true,
                    authorities
            );
        }

}
