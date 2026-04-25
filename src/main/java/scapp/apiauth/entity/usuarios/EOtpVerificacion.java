package scapp.apiauth.entity.usuarios;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import scapp.apiauth.entity.ETipoOtp;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verificacion")
@Getter
@Setter
public class EOtpVerificacion {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "codigo", nullable = false, length = 6)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private ETipoOtp tipo;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "usado", nullable = false)
    private Boolean usado;

    @Column(name = "fecha_uso")
    private LocalDateTime fechaUso;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.tipo == null) {
            this.tipo = ETipoOtp.VERIFICACION_CORREO;
        }

        if (this.usado == null) {
            this.usado = Boolean.FALSE;
        }

        this.createdAt = LocalDateTime.now();
    }


}
