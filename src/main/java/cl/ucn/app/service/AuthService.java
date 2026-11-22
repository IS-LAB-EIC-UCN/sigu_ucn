package cl.ucn.app.service;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;

public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService() {
        this(new UsuarioRepository());
    }

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario autenticar(String correo, String password) {
        if (correo == null || correo.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        Usuario usuario = usuarioRepository.findByCorreo(correo.strip().toLowerCase(java.util.Locale.ROOT));

        if (usuario == null) {
            return null;
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            return null;
        }

        if (!PasswordHasher.verificar(password, usuario.getPassword())) {
            return null;
        }

        return usuario;
    }
}
