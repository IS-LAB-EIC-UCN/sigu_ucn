package cl.ucn.app.service.biblioteca.api;

import cl.ucn.app.model.biblioteca.PrestamoLibro;

import java.time.LocalDate;

public interface IPrestamoService {
    PrestamoLibro solicitarPrestamo(Long lectorId, Long libroId, LocalDate fechaVencimiento);
    PrestamoLibro solicitarPrestamoActivo(Long lectorId, Long libroId, LocalDate fechaVencimiento);
    void confirmarEntrega(Long prestamoId);
    void solicitarDevolucion(Long prestamoId);
    void confirmarDevolucion(Long prestamoId);
}
