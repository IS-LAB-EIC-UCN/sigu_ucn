package cl.ucn.app.service;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;

public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Usuario autenticar(String correo, String password) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            return null;
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            return null;
        }

        // Por ahora comparación simple.
        // Más adelante, se podría reemplazar por hash seguro.
        if (!usuario.getPassword().equals(password)) {
            return null;
        }

        return usuario;
    }
}
