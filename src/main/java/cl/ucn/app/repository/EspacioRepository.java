package cl.ucn.app.repository;

import cl.ucn.app.model.Espacio;
import java.util.List;
import java.util.Optional;

public interface EspacioRepository {
    void save(Espacio espacio);
    Optional<Espacio> findById(String id);
    List<Espacio> findAll();
    List<Espacio> findByTipo(String tipo);
}
