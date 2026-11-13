package cl.ucn.app.repository;

import cl.ucn.app.model.Estudiante;

import java.util.Optional;

public interface EstudianteRepository {

    Optional<Estudiante> findByCorreo(String correo);

    Optional<Estudiante> findByUsuarioId(Long usuarioId);
}
