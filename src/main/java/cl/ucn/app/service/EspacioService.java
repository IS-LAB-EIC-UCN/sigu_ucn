package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.EventoRepository;

import java.util.List;

public class EspacioService {

    private final EspacioRepository espacioRepository;
    private final EventoRepository eventoRepository;

    public EspacioService() {
        this.espacioRepository = new EspacioRepository();
        this.eventoRepository = new EventoRepository();
    }

    public Espacio registrar(Espacio espacio) {
        if (espacio.getNombre() == null || espacio.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del espacio es obligatorio.");
        }
        if (espacio.getTipo() == null || espacio.getTipo().isBlank()) {
            throw new IllegalArgumentException("El tipo de espacio es obligatorio.");
        }
        if (espacio.getCapacidad() == null || espacio.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser un número positivo.");
        }
        if (espacio.getDisponible() == null) {
            espacio.setDisponible(true);
        }

        List<Espacio> existentes = espacioRepository.findAll();
        for (Espacio e : existentes) {
            if (e.getNombre().equalsIgnoreCase(espacio.getNombre().trim())) {
                throw new IllegalArgumentException("Ya existe un espacio con el nombre \"" + espacio.getNombre() + "\".");
            }
        }

        return espacioRepository.save(espacio);
    }

    public Espacio actualizar(Long id, String nombre, String tipo, Integer capacidad, Boolean disponible) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del espacio es obligatorio.");
        }
        Espacio espacio = buscarPorId(id);

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del espacio es obligatorio.");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo de espacio es obligatorio.");
        }
        if (capacidad == null || capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser un número positivo.");
        }

        List<Espacio> existentes = espacioRepository.findAll();
        for (Espacio e : existentes) {
            if (!e.getId().equals(id) && e.getNombre().equalsIgnoreCase(nombre.trim())) {
                throw new IllegalArgumentException("Ya existe otro espacio con el nombre \"" + nombre + "\".");
            }
        }

        espacio.setNombre(nombre.trim());
        espacio.setTipo(tipo);
        espacio.setCapacidad(capacidad);
        espacio.setDisponible(disponible != null && disponible);

        return espacioRepository.save(espacio);
    }

    public Espacio buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del espacio es obligatorio.");
        }
        Espacio espacio = espacioRepository.findById(id);
        if (espacio == null) {
            throw new IllegalArgumentException("Espacio no encontrado con ID: " + id);
        }
        return espacio;
    }

    public List<Espacio> listarTodos() {
        return espacioRepository.findAll();
    }

    public void eliminar(Long id) {
        Espacio espacio = buscarPorId(id);

        boolean tieneEventos = eventoRepository.existenEventosActivosPorEspacio(id);
        if (tieneEventos) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el espacio \"" + espacio.getNombre() +
                    "\" porque tiene eventos activos asociados.");
        }

        espacioRepository.delete(espacio);
    }
}
