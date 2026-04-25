package scapp.apiauth.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String correo;
    private String password;
}
