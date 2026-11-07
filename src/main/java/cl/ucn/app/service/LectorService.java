package cl.ucn.app.service;

import cl.ucn.app.model.Lector;
import cl.ucn.app.model.TipoLector;

import java.util.List;
import java.util.Optional;

public interface LectorService {
    Lector registrarLector(String rut, String nombre, String correo, TipoLector tipo);
    Optional<Lector> buscarPorId(Long id);
    Optional<Lector> buscarPorRut(String rut);
    Optional<Lector> buscarPorCorreo(String correo);
    List<Lector> listarTodos();
}
