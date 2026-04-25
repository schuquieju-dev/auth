package scapp.apiauth.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompletarPerfilRequest {


    private Long usuarioId;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String dpi;
    private String fotoDpiFrontal;
    private String fotoDpiTrasera;
}
