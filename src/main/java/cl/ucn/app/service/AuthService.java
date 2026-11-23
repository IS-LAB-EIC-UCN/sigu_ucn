package cl.ucn.app.service;

import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.security.PasswordHasher;
import cl.ucn.app.security.Pbkdf2PasswordHasher;

import java.util.Locale;
import java.util.Objects;

public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

    public AuthService() {
        this(new UsuarioRepository(), new Pbkdf2PasswordHasher());
    }

    /** Constructor con dependencias explícitas (DIP), usado también en las pruebas. */
    public AuthService(UsuarioRepository usuarioRepository, PasswordHasher passwordHasher) {
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
    }

    public Usuario autenticar(String correo, String password) {
        if (correo == null || password == null) {
            return null;
        }

        Usuario usuario = usuarioRepository.findByCorreo(normalizarCorreo(correo));

        if (usuario == null) {
            return null;
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            return null;
        }

        if (!passwordValida(password, usuario.getPassword())) {
            return null;
        }

        return usuario;
    }

    /** Correo en minúsculas y sin espacios, para que el login no dependa de mayúsculas. */
    static String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * RNF-01: las contraseñas se verifican contra su hash.
     * Compatibilidad temporal: si una base local aún no ejecutó 12_usuarios_hash.sql,
     * los usuarios de 02_seed.sql tienen la contraseña en texto plano y se comparan directamente.
     */
    private boolean passwordValida(String password, String almacenada) {
        if (almacenada == null) {
            return false;
        }
        if (passwordHasher.esHash(almacenada)) {
            return passwordHasher.verificar(password, almacenada);
        }
        return almacenada.equals(password);
    }
}
