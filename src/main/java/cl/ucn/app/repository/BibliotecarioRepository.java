package cl.ucn.app.repository;

import cl.ucn.app.model.Bibliotecario;
import java.util.List;
import java.util.Optional;

public interface BibliotecarioRepository {
    Optional<Bibliotecario> findById(Long id);
    Optional<Bibliotecario> findByRut(String rut);
    Optional<Bibliotecario> findByCorreo(String correo);
    List<Bibliotecario> findAll();
    Bibliotecario save(Bibliotecario bibliotecario);
}
