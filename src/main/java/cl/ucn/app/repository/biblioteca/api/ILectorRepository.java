package cl.ucn.app.repository.biblioteca.api;

import cl.ucn.app.model.biblioteca.Lector;

import java.util.List;

public interface ILectorRepository {
    void save(Lector lector);
    void save(Lector lector, jakarta.persistence.EntityManager em);
    void delete(Lector lector);
    Lector findById(Long id);
    Lector findByRut(String rut);
    Lector findByCorreo(String correo);
    List<Lector> findAll();
}
