package scapp.apiauth.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {
    private String correo;
    private String codigo;

}
