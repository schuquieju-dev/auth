package scapp.apiauth.dto.response;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LoginResponse {


    private Long usuarioId;
    private Long personaId;
    private String correo;
    private String estado;
    private String token;
    private List<String> roles;
}
