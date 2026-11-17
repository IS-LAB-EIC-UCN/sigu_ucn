package cl.ucn.app.repository.soporte;

import cl.ucn.app.model.soporte.TecnicoDisponible;

import java.util.List;

public interface PermisosSoporteRepository {
    boolean esAdministrador(Long usuarioId);

    boolean esTecnico(Long usuarioId);

    List<TecnicoDisponible> listarTecnicosActivos();
}
