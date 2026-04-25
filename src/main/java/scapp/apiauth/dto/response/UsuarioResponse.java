package scapp.apiauth.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsuarioResponse {


    private Long id;
    private Long personaId;
    private String correo;
    private Boolean correoVerificado;
    private String estado;
    private Boolean bloqueado;
}
