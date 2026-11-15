package cl.ucn.app.repository;

import cl.ucn.app.model.Estudiante;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public interface EstudianteRepository {

    Optional<Estudiante> findByCorreo(String correo);

    Optional<Estudiante> findByUsuarioId(Long usuarioId);

    // ---- Dentro de una transacción ----

    Optional<Estudiante> findByUsuarioId(EntityManager em, Long usuarioId);

    boolean cursaAsignatura(EntityManager em, Long estudianteId, Long asignaturaId);
}
