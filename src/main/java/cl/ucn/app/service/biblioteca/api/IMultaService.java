package cl.ucn.app.service.biblioteca.api;

import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;

public interface IMultaService {
    Multa generarMulta(PrestamoLibro prestamo, int diasAtraso);
    void registrarPago(Long multaId);
}
