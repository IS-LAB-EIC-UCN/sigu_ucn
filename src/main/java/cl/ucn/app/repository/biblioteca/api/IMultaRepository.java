package cl.ucn.app.repository.biblioteca.api;

import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Multa;

import java.util.List;

public interface IMultaRepository {
    void save(Multa multa);
    void save(Multa multa, jakarta.persistence.EntityManager em);
    Multa findById(Long id);
    Multa findByPrestamo(cl.ucn.app.model.biblioteca.PrestamoLibro prestamo);
    Multa findByPrestamo(cl.ucn.app.model.biblioteca.PrestamoLibro prestamo, jakarta.persistence.EntityManager em);
    List<Multa> findPendientesByLector(Lector lector);
    List<Multa> findAll();
}
