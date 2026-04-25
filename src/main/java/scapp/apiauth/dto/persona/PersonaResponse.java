package scapp.apiauth.dto.persona;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class PersonaResponse {
    private Long id;
    private String cui;
    private String nit;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String telefono;
    private String email;
    private String sexo;
    private String direccion;

    private LocalDate fechaNacimiento;

    private Boolean activo;
    private String nombreCompleto;
}