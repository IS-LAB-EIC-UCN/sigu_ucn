package cl.ucn.app.service.biblioteca.api;

import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import jakarta.persistence.EntityManager;

public interface IMultaService {
    Multa generarMulta(PrestamoLibro prestamo, int diasAtraso);
    Multa generarMulta(PrestamoLibro prestamo, int diasAtraso, EntityManager em);
    void registrarPago(Long multaId);
}
