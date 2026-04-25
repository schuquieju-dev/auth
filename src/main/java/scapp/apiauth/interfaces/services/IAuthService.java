package scapp.apiauth.interfaces.services;

import scapp.apiauth.dto.request.*;
import scapp.apiauth.dto.response.AuthMessageResponse;
import scapp.apiauth.dto.response.LoginResponse;
import scapp.apiauth.dto.response.UsuarioResponse;



public interface IAuthService {

    void register(RegisterRequest request);


    LoginResponse verifyOtp(VerifyOtpRequest request);
    void resendOtp(ResendOtpRequest request);

    LoginResponse login(LoginRequest request);

    void completarPerfil(String correo, CompletarPerfilRequest request);

    UsuarioResponse findByCorreo(String correo);

    UsuarioResponse me(String correo);
}


