package scapp.apiauth.config.segurity;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import scapp.apiauth.entity.usuarios.EUsuario;
import scapp.apiauth.interfaces.repository.IUsuarioRepository;
import scapp.apiauth.interfaces.services.IJwtService;
import scapp.apiauth.interfaces.services.IUsuarioDetailsService;

import java.io.IOException;


@Component
@RequiredArgsConstructor

public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final IJwtService jwtService;
    private final IUsuarioDetailsService usuarioDetailsService;
    private final IUsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String correo;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        correo = jwtService.extractUsername(jwt);

        if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = usuarioDetailsService.loadUserByUsername(correo);

            EUsuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElse(null);

            if (usuario != null && jwtService.isTokenValid(jwt, usuario)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}