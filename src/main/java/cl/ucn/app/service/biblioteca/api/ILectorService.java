package cl.ucn.app.service.biblioteca.api;

import cl.ucn.app.model.biblioteca.Lector;

import java.util.List;

public interface ILectorService {
    Lector registrarLector(String nombre, String correo, String rut);
    Lector buscarPorRut(String rut);
    Lector buscarPorId(Long id);
    List<Lector> listarTodos();
    Lector actualizarLector(Long id, String nombre, String correo, String rut);
    void bloquearLector(Long id);
    void desbloquearLector(Long id);
    void eliminarLector(Long id);
    boolean tieneDeudaPendiente(Long lectorId);
    java.util.List<cl.ucn.app.model.biblioteca.Multa> obtenerMultas(Long lectorId);
    Lector crearDesdeUsuario(String nombre, String correo, String rut);
}
