package scapp.apiauth.interfaces.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scapp.apiauth.entity.usuarios.EUsuario;

import java.util.Optional;


@Repository
public interface IUsuarioRepository extends JpaRepository<EUsuario, Long> {

    Optional<EUsuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
