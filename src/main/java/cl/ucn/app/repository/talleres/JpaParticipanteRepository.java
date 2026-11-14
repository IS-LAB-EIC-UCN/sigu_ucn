package cl.ucn.app.repository.talleres;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;

import java.util.Optional;

/** Adapta el UsuarioRepository del sistema base a la interfaz que necesita el módulo de talleres. */
public class JpaParticipanteRepository implements ParticipanteRepository {

    private final UsuarioRepository usuarioRepository;

    public JpaParticipanteRepository(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return Optional.ofNullable(usuarioRepository.findById(id));
    }
}
