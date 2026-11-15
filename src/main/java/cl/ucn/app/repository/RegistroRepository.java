package cl.ucn.app.repository;

import cl.ucn.app.model.Estudiante;
import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;

public interface RegistroRepository {

    boolean existeUsuarioConCorreo(String correo);

    boolean existeEstudianteConCorreo(String correo);

    Rol buscarRol(String nombre);

    void guardar(Usuario usuario, Estudiante estudiante);
}
