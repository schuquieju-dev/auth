package scapp.apiauth.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import scapp.apiauth.config.MailProperties;
import scapp.apiauth.interfaces.services.IEmailService;


@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {


    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void enviarOtpRegistro(String destino, String codigo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(destino);
        message.setSubject("Código de verificación de correo");
        message.setText(
                "Hola,\n\n" +
                        "Tu código de verificación es: " + codigo + "\n\n" +
                        "Este código vence en 15 minutos y solo puede usarse una vez.\n\n" +
                        "Schuquiej :)\n\n" +
                        "SCApp"
        );

        mailSender.send(message);
    }

    @Override
    public void enviarOtpRecuperacion(String destino, String codigo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(destino);
        message.setSubject("Código de recuperación de contraseña");
        message.setText(
                "Hola,\n\n" +
                        "Tu código de recuperación es: " + codigo + "\n\n" +
                        "Este código vence en 15 minutos y solo puede usarse una vez.\n\n" +
                        "Schuquiej :)\n\n" +
                        "SCApp"
        );

        mailSender.send(message);
    }
}