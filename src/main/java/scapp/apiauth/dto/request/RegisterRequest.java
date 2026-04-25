package scapp.apiauth.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String correo;
    private String password;
}
