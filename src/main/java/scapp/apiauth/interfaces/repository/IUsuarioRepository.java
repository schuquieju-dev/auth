package scapp.apiauth.interfaces.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import scapp.apiauth.entity.usuarios.EUsuario;

import java.util.List;
import java.util.Optional;


@Repository
public interface IUsuarioRepository extends JpaRepository<EUsuario, Long> {

    Optional<EUsuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    // Consulta nativa para obtener los códigos de los roles activos del usuario
    @Query(value = "SELECT r.codigo FROM public.usuario u " +
            "INNER JOIN public.persona_rol pr ON u.persona_id = pr.persona_id " +
            "INNER JOIN public.rol r ON pr.rol_id = r.id " +
            "WHERE LOWER(u.correo) = LOWER(:correo) AND r.activo = true",
            nativeQuery = true)
    List<String> findRolesByCorreo(@Param("correo") String correo);


}
