package cl.ucn.app.service;

import cl.ucn.app.service.Interfaces.IUsuario;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;
import java.util.Optional;

public class UsuarioService implements IUsuario {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<Usuario> obtenerPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(usuarioRepository.findByEmail(email));
    }
}
