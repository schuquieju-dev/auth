package scapp.apiauth.entity.usuarios;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import scapp.apiauth.entity.EEstadoUsuario;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
public class EUsuario {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "persona_id")
    private Long personaId;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 40)
    private EEstadoUsuario estado;

    @Column(name = "correo_verificado", nullable = false)
    private Boolean correoVerificado;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(name = "intentos_fallidos", nullable = false)
    private Integer intentosFallidos;

    @Column(name = "bloqueado", nullable = false)
    private Boolean bloqueado;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (this.estado == null) {
            this.estado = EEstadoUsuario.PENDIENTE_VERIFICACION_CORREO;
        }

        if (this.correoVerificado == null) {
            this.correoVerificado = Boolean.FALSE;
        }

        if (this.intentosFallidos == null) {
            this.intentosFallidos = 0;
        }

        if (this.bloqueado == null) {
            this.bloqueado = Boolean.FALSE;
        }

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}