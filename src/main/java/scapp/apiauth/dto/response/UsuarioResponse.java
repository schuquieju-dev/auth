package scapp.apiauth.dto.response;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UsuarioResponse {


    private Long id;
    private Long personaId;
    private String correo;
    private Boolean correoVerificado;
    private String estado;
    private Boolean bloqueado;
    private List<String> roles;
}
