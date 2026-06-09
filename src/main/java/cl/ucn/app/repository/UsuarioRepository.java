package cl.ucn.app.repository;

import cl.ucn.app.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
    void save(Usuario usuario);
    Optional<Usuario> findById(String id);
    Optional<Usuario> findByEmail(String email);
}
