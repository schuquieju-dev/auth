package scapp.apiauth.config.segurity;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// IMPORTANTE: Nuevos imports para el CORS
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. AÑADIMOS LA CONFIGURACIÓN CORS AQUÍ PRIMERO
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> {
                    csrf.disable();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/api/auth/register",
                            "/api/auth/verify-otp",
                            "/api/auth/resend-otp",
                            "/api/auth/login",
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs",
                            "/v3/api-docs/**",
                            "/v3/api-docs/swagger-config",
                            "/v3/api-docs.yaml"
                    ).permitAll();

                    // Importante: Spring Security automáticamente permitirá las peticiones OPTIONS con la config del CORS
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. CREAMOS EL BEAN DE CORS DIRECTAMENTE AQUÍ (Compatible con Java 17)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permite solicitudes desde tu frontend en Angular
        configuration.setAllowedOriginPatterns(List.of("*")); // o usa "http://localhost:4200" por seguridad extra

        // Permite todos los métodos, en especial el OPTIONS que genera el navegador
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Permite cualquier cabecera (incluyendo Authorization para tu token)
        configuration.setAllowedHeaders(List.of("*"));

        // Permite credenciales (necesario cuando se mandan tokens)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}