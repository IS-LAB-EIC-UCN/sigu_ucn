package cl.ucn.app.service.biblioteca.api;

import cl.ucn.app.model.biblioteca.PrestamoLibro;

import java.util.List;

public interface IHistorialService {
    List<PrestamoLibro> obtenerHistorial(Long lectorId);
    List<PrestamoLibro> obtenerTodosLosPrestamos();
}
