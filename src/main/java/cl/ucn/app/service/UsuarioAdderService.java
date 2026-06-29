package cl.ucn.app.service;

import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;

public class UsuarioAdderService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioAdderService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public boolean crearUsuario(String nombre, String correo, String password, Boolean activo, Rol rol) {
        Usuario usuario = new Usuario(nombre, correo, password, activo, rol);
        usuarioRepository.save(usuario);
        return true;
    }
}
