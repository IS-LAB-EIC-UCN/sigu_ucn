package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.repository.EspacioRepository;
import java.util.List;
import java.util.Optional;

public class EspacioService {
    private final EspacioRepository espacioRepository;

    public EspacioService(EspacioRepository espacioRepository) {
        this.espacioRepository = espacioRepository;
    }

    public List<Espacio> obtenerTodos() {
        return espacioRepository.findAll();
    }

    public List<Espacio> obtenerPorTipo(String tipo) {
        return espacioRepository.findByTipo(tipo);
    }

    public Optional<Espacio> obtenerPorId(Long id) {
        return espacioRepository.findById(id);
    }
}
