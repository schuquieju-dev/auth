package scapp.apiauth.interfaces.services;

public interface IEmailService {

    void enviarOtpRegistro(String destino, String codigo);

    void enviarOtpRecuperacion(String destino, String codigo);

}