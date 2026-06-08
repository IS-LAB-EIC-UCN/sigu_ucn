package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.repository.EspacioRepository;

import java.util.List;

public class EstacionamientoService {

    private final EspacioRepository espacioRepository;

    public EstacionamientoService() {
        this.espacioRepository = new EspacioRepository();
    }

    public List<Espacio> obtenerEspacios() {
        return espacioRepository.findAll();
    }

    public Espacio obtenerPorId(Long id) {
        return espacioRepository.findById(id);
    }

    public void guardar(Espacio espacio) {
        espacioRepository.save(espacio);
    }

    public void eliminar(Long id) {
        espacioRepository.delete(id);
    }

    public void actualizar(Espacio espacio) {
        espacioRepository.update(espacio);
    }

}