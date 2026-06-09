package cl.ucn.app.service;

import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;
import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registrar(String nombre, String email, String password, Rol rol) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        Usuario usuario = new Usuario(UUID.randomUUID().toString(), nombre, email, password, rol);
        usuarioRepository.save(usuario);
        return usuario;
    }

    public Optional<Usuario> login(String email, String password) {
        return usuarioRepository.findByEmail(email)
            .filter(u -> u.getPassword().equals(password));
    }
}
