package cl.ucn.app.service.biblioteca.api;

import cl.ucn.app.model.biblioteca.PrestamoLibro;

import java.time.LocalDate;

public interface IPrestamoService {
    PrestamoLibro solicitarPrestamo(Long lectorId, Long libroId, LocalDate fechaVencimiento);
}
