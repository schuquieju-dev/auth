package scapp.apiauth.dto.persona;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PersonaCreateRequest {

    @NotBlank(message = "El CUI es requerido.")
    @Pattern(regexp = "^[0-9]{13}$", message = "El CUI debe tener 13 dígitos.")
    private String cui;

    @Pattern(regexp = "^[0-9]{13}$", message = "El NIT no tiene formato válido.")
    private String nit;

    @NotBlank(message = "El primer nombre es requerido.")
    @Size(max = 80, message = "El primer nombre no debe exceder 80 caracteres.")
    private String primerNombre;

    @Size(max = 80, message = "El segundo nombre no debe exceder 80 caracteres.")
    private String segundoNombre;

    @NotBlank(message = "El primer apellido es requerido.")
    @Size(max = 80, message = "El primer apellido no debe exceder 80 caracteres.")
    private String primerApellido;

    @Size(max = 80, message = "El segundo apellido no debe exceder 80 caracteres.")
    private String segundoApellido;

    @Size(max = 20, message = "El teléfono no debe exceder 20 caracteres.")
    private String telefono;

    @Email(message = "El correo no es válido.")
    @Size(max = 150, message = "El correo no debe exceder 150 caracteres.")
    private String email;


    @NotNull(message = "Fecha de nacimiento requerida.")
    private LocalDate fechaNacimiento;


    @NotBlank(message = "Sexo de nacimiento requerida.")
    private String sexo;


    @NotBlank(message = "Dirección de nacimiento requerida.")
    private String direccion;


}
