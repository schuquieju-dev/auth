package scapp.apiauth.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scapp.apiauth.dto.persona.PersonaCreateRequest;
import scapp.apiauth.dto.persona.PersonaResponse;
import scapp.apiauth.dto.request.*;
import scapp.apiauth.dto.response.LoginResponse;
import scapp.apiauth.dto.response.UsuarioResponse;
import scapp.apiauth.entity.EEstadoUsuario;
import scapp.apiauth.entity.ETipoOtp;
import scapp.apiauth.entity.usuarios.EOtpVerificacion;
import scapp.apiauth.entity.usuarios.EUsuario;
import scapp.apiauth.interfaces.repository.IOtpVerificacionRepository;
import scapp.apiauth.interfaces.repository.IUsuarioRepository;
import scapp.apiauth.interfaces.services.IAuthService;
import scapp.apiauth.interfaces.services.IEmailService;
import scapp.apiauth.interfaces.services.IJwtService;
import scapp.apiauth.interfaces.services.persona.IPersonaClientService;
import scapp.apiauth.util.OtpUtil;
import scapp.apiauth.util.BusinessException;
import scapp.apiauth.util.ResourceNotFoundException;
import scapp.apiauth.util.UnauthorizedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements IAuthService {

    private final IUsuarioRepository usuarioRepository;
    private final IOtpVerificacionRepository otpVerificacionRepository;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService jwtService;
    private final IEmailService emailService;
    private final IPersonaClientService personaClientService;

    @Override
    public void register(RegisterRequest request) {
        String correo = request.getCorreo().trim().toLowerCase();

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new BusinessException("El correo ya se encuentra registrado.");
        }

        EUsuario usuario = new EUsuario();
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEstado(EEstadoUsuario.PENDIENTE_VERIFICACION_CORREO);
        usuario.setCorreoVerificado(Boolean.FALSE);
        usuario.setBloqueado(Boolean.FALSE);
        usuario.setIntentosFallidos(0);

        // Intentamos la vinculación imprimiendo trazas de control
        try {
            System.out.println("[AuthRegister] Iniciando búsqueda de vinculación para: " + correo);
            PersonaResponse personaExistente = personaClientService.obtenerPorEmail(correo);

            if (personaExistente != null && personaExistente.getId() != null) {
                System.out.println("[AuthRegister] ¡Se detectó coincidencia! Vinculando Persona ID: " + personaExistente.getId());
                usuario.setPersonaId(personaExistente.getId());
            } else {
                System.out.println("[AuthRegister] No se encontró ninguna persona previa para este correo.");
            }
        } catch (Exception e) {
            System.err.println("[AuthRegister] Error al intentar asignar la persona en el registro:");
            e.printStackTrace();
        }

        EUsuario usuarioGuardado = usuarioRepository.save(usuario);
        System.out.println("[AuthRegister] Usuario guardado en DB con Persona ID: " + usuarioGuardado.getPersonaId());

        EOtpVerificacion otp = crearOtp(usuarioGuardado.getId(), ETipoOtp.VERIFICACION_CORREO);
        emailService.enviarOtpRegistro(usuarioGuardado.getCorreo(), otp.getCodigo());
    }

    @Override
    public LoginResponse verifyOtp(VerifyOtpRequest request) {
        EUsuario usuario = usuarioRepository.findByCorreo(request.getCorreo().trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        EOtpVerificacion otp = otpVerificacionRepository
                .findTopByUsuarioIdAndCodigoAndTipoAndUsadoFalseOrderByIdDesc(
                        usuario.getId(),
                        request.getCodigo(),
                        ETipoOtp.VERIFICACION_CORREO
                )
                .orElseThrow(() -> new BusinessException("OTP inválido."));

        if (otp.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El OTP ha expirado.");
        }

        otp.setUsado(Boolean.TRUE);
        otp.setFechaUso(LocalDateTime.now());
        usuario.setCorreoVerificado(Boolean.TRUE);

        // BIFURCACIÓN INTELIGENTE: ¿Es transportista/usuario precargado o usuario nuevo?
        if (usuario.getPersonaId() != null) {
            usuario.setEstado(EEstadoUsuario.PENDIENTE_APROBACION);
        } else {
            usuario.setEstado(EEstadoUsuario.PERFIL_INCOMPLETO);
        }

        otpVerificacionRepository.save(otp);
        EUsuario usuarioActualizado = usuarioRepository.save(usuario);

        // =========================================================================
        // CONFLICTO RESUELTO: Mantenemos la carga dinámica de roles reales de la DB
        // =========================================================================
        List<String> roles = usuarioRepository.findRolesByCorreo(usuarioActualizado.getCorreo());
        String token = jwtService.generateToken(usuarioActualizado, roles);

        return LoginResponse.builder()
                .usuarioId(usuarioActualizado.getId())
                .personaId(usuarioActualizado.getPersonaId())
                .correo(usuarioActualizado.getCorreo())
                .estado(usuarioActualizado.getEstado().name())
                .roles(roles)
                .token(token)
                .build();
    }

    @Override
    public void resendOtp(ResendOtpRequest request) {
        String correo = request.getCorreo().trim().toLowerCase();

        EUsuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        if (Boolean.TRUE.equals(usuario.getCorreoVerificado())) {
            throw new BusinessException("El correo ya se encuentra verificado.");
        }

        EOtpVerificacion otp = crearOtp(usuario.getId(), ETipoOtp.VERIFICACION_CORREO);
        emailService.enviarOtpRegistro(usuario.getCorreo(), otp.getCodigo());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String correo = request.getCorreo().trim().toLowerCase();

        EUsuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas."));

        if (Boolean.TRUE.equals(usuario.getBloqueado())) {
            throw new UnauthorizedException("El usuario se encuentra bloqueado.");
        }

        if (usuario.getEstado() == EEstadoUsuario.PENDIENTE_VERIFICACION_CORREO) {
            throw new BusinessException("Debe verificar su correo antes de iniciar sesión.");
        }

        if (usuario.getEstado() == EEstadoUsuario.RECHAZADO || usuario.getEstado() == EEstadoUsuario.INACTIVO) {
            throw new UnauthorizedException("El usuario no tiene acceso al sistema.");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);

            if (usuario.getIntentosFallidos() >= 5) {
                usuario.setBloqueado(Boolean.TRUE);
            }

            usuarioRepository.save(usuario);
            throw new UnauthorizedException("Credenciales inválidas.");
        }

        usuario.setIntentosFallidos(0);
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // --- CARGAR ROLES ADICIONADOS ---
        List<String> roles = usuarioRepository.findRolesByCorreo(usuario.getCorreo());
        String token = jwtService.generateToken(usuario, roles);

        return LoginResponse.builder()
                .usuarioId(usuario.getId())
                .personaId(usuario.getPersonaId())
                .correo(usuario.getCorreo())
                .estado(usuario.getEstado().name())
                .roles(roles)
                .token(token)
                .build();
    }

    @Override
    public void completarPerfil(String correo, CompletarPerfilRequest request) {
        EUsuario usuario = usuarioRepository.findByCorreo(correo.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        if (!Boolean.TRUE.equals(usuario.getCorreoVerificado())) {
            throw new BusinessException("Debe verificar su correo antes de completar perfil.");
        }

        if (usuario.getPersonaId() != null) {
            throw new BusinessException("El usuario ya tiene una persona asociada.");
        }

        PersonaCreateRequest personaCreateRequest = new PersonaCreateRequest();
        personaCreateRequest.setCui(request.getCui());
        personaCreateRequest.setNit(request.getNit());
        personaCreateRequest.setPrimerNombre(request.getPrimerNombre());
        personaCreateRequest.setSegundoNombre(request.getSegundoNombre());
        personaCreateRequest.setPrimerApellido(request.getPrimerApellido());
        personaCreateRequest.setSegundoApellido(request.getSegundoApellido());
        personaCreateRequest.setTelefono(request.getTelefono());
        personaCreateRequest.setEmail(usuario.getCorreo());
        personaCreateRequest.setSexo(request.getSexo());
        personaCreateRequest.setDireccion(request.getDireccion());
        personaCreateRequest.setFechaNacimiento(request.getFechaNacimiento());

        PersonaResponse personaResponse = personaClientService.crearPersona(personaCreateRequest);

        if (personaResponse == null || personaResponse.getId() == null) {
            throw new RuntimeException("No fue posible registrar la persona en el microservicio correspondiente.");
        }

        usuario.setPersonaId(personaResponse.getId());
        usuario.setEstado(EEstadoUsuario.PENDIENTE_APROBACION);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findByCorreo(String correo) {
        EUsuario usuario = usuarioRepository.findByCorreo(correo.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        List<String> roles = usuarioRepository.findRolesByCorreo(usuario.getCorreo());

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .personaId(usuario.getPersonaId())
                .correo(usuario.getCorreo())
                .correoVerificado(usuario.getCorreoVerificado())
                .estado(usuario.getEstado().name())
                .bloqueado(usuario.getBloqueado())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse me(String correo) {
        return findByCorreo(correo);
    }

    private EOtpVerificacion crearOtp(Long usuarioId, ETipoOtp tipoOtp) {
        EOtpVerificacion otp = new EOtpVerificacion();
        otp.setUsuarioId(usuarioId);
        otp.setCodigo(OtpUtil.generarCodigo6Digitos());
        otp.setTipo(tipoOtp);
        otp.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));
        otp.setUsado(Boolean.FALSE);

        return otpVerificacionRepository.save(otp);
    }
}