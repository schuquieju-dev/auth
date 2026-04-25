package scapp.apiauth.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;



@Getter
@Setter
public class CompletarPerfilRequest {
    private String cui;
    private String nit;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String telefono;
    private String sexo;
    private String direccion;
    private LocalDate fechaNacimiento;
}
