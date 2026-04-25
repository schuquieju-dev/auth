package scapp.apiauth.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {


    private Long usuarioId;
    private Long personaId;
    private String correo;
    private String estado;
    private String token;
}
