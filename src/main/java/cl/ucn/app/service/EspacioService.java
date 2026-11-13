package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.repository.EspacioRepository;

import java.util.List;

public class EspacioService {

    private final EspacioRepository espacioRepository;

    public EspacioService(EspacioRepository espacioRepository) {
        this.espacioRepository = espacioRepository;
    }

    public List<Espacio> listarDisponibles() {
        return espacioRepository.findDisponibles();
    }
}