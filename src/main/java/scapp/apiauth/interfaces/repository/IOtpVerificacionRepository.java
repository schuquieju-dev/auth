package scapp.apiauth.interfaces.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scapp.apiauth.entity.ETipoOtp;
import scapp.apiauth.entity.usuarios.EOtpVerificacion;

import java.util.Optional;


@Repository
public interface IOtpVerificacionRepository extends JpaRepository<EOtpVerificacion,Long> {

    Optional<EOtpVerificacion> findTopByUsuarioIdAndTipoOrderByIdDesc(Long usuarioId, ETipoOtp tipo);

    Optional<EOtpVerificacion> findTopByUsuarioIdAndCodigoAndTipoAndUsadoFalseOrderByIdDesc(
            Long usuarioId,
            String codigo,
            ETipoOtp tipo
    );

}
