package cl.ucn.app.service;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;

public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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

    public Usuario registrar(String nombre, String email, String password, String rolNombre) {
        if (usuarioRepository.findByCorreo(email) != null) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        String mappedRol = rolNombre.toUpperCase();
        if (mappedRol.equals("ADMINISTRADOR")) {
            mappedRol = "ADMIN";
        } else if (mappedRol.equals("PROFESOR")) {
            mappedRol = "DOCENTE";
        }

        EntityManager em = JPAUtil.getEntityManager();
        Rol rolEntity;
        try {
            rolEntity = em.createQuery("SELECT r FROM Rol r WHERE r.nombre = :nombre", Rol.class)
                    .setParameter("nombre", mappedRol)
                    .getSingleResult();
        } catch (Exception e) {
            throw new IllegalArgumentException("Rol inválido o no soportado: " + rolNombre);
        } finally {
            em.close();
        }

        Usuario usuario = new Usuario(nombre, email, password, true, rolEntity);
        usuarioRepository.save(usuario);
        return usuario;
    }

    public Optional<Usuario> login(String email, String password) {
        Usuario u = usuarioRepository.findByCorreo(email);
        if (u != null && u.getPassword().equals(password) && Boolean.TRUE.equals(u.getActivo())) {
            return Optional.of(u);
        }
        return Optional.empty();
    }
}
