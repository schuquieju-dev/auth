package scapp.apiauth.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import scapp.apiauth.dto.request.*;
import scapp.apiauth.dto.response.ApiResponseDto;
import scapp.apiauth.dto.response.LoginResponse;
import scapp.apiauth.dto.response.UsuarioResponse;
import scapp.apiauth.interfaces.services.IAuthService;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {




    private final IAuthService authService;

    @PostMapping("/register")
    public ApiResponseDto<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponseDto.success("Usuario registrado. OTP enviado correctamente al correo.", null);
    }



    @PostMapping("/verify-otp")
    public ApiResponseDto<LoginResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        // Guardamos la respuesta del servicio en una variable
        LoginResponse response = authService.verifyOtp(request);

        // La pasamos como el "data" de tu respuesta genérica
        return ApiResponseDto.success("Correo verificado correctamente.", response);
    }


    @PostMapping("/resend-otp")
    public ApiResponseDto<Void> resendOtp(@RequestBody ResendOtpRequest request) {
        authService.resendOtp(request);
        return ApiResponseDto.success("OTP reenviado correctamente al correo.", null);
    }

    @PostMapping("/login")
    public ApiResponseDto<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponseDto.success("Inicio de sesión exitoso.", response);
    }

    @PostMapping("/completar-perfil")
    public ApiResponseDto<Void> completarPerfil(
            @RequestBody CompletarPerfilRequest request,
            Authentication authentication
    ) {
        authService.completarPerfil(authentication.getName(), request);
        return ApiResponseDto.success("Perfil completado correctamente. Pendiente de aprobación.", null);
    }

    @GetMapping("/usuario")
    public ApiResponseDto<UsuarioResponse> findByCorreo(@RequestParam String correo) {
        UsuarioResponse response = authService.findByCorreo(correo);
        return ApiResponseDto.success("Usuario obtenido correctamente.", response);
    }

    @GetMapping("/me")
    public ApiResponseDto<UsuarioResponse> me(Authentication authentication) {
        UsuarioResponse response = authService.me(authentication.getName());
        return ApiResponseDto.success("Usuario autenticado obtenido correctamente.", response);
    }

}