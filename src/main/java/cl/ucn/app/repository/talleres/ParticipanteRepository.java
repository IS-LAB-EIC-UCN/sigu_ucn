package cl.ucn.app.repository.talleres;

import cl.ucn.app.model.Usuario;

import java.util.Optional;

/**
 * Lo único que el módulo de talleres necesita de los usuarios: buscarlos por id.
 * Interfaz pequeña y específica (ISP); los participantes son los usuarios del sistema base.
 */
public interface ParticipanteRepository {

    Optional<Usuario> findById(Long id);
}
