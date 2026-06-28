package cl.ucn.app.repository.biblioteca.api;

import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.PrestamoLibro;

import java.util.List;

public interface IPrestamoLibroRepository {
    void save(PrestamoLibro prestamo);
    void save(PrestamoLibro prestamo, jakarta.persistence.EntityManager em);
    PrestamoLibro findById(Long id);
    List<PrestamoLibro> findByLector(Lector lector);
    PrestamoLibro findActivoByEjemplar(Ejemplar ejemplar);
    List<PrestamoLibro> findAll();
}
