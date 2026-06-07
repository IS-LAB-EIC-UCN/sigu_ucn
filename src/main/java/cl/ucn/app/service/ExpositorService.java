package cl.ucn.app.service;

import cl.ucn.app.model.Expositor;
import cl.ucn.app.repository.ExpositorRepository;

import java.util.List;

public class ExpositorService {

    private final ExpositorRepository expositorRepository;

    public ExpositorService() {
        this.expositorRepository = new ExpositorRepository();
    }

    public Expositor registrar(Expositor expositor) {
        if (expositor.getNombre() == null || expositor.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del expositor es obligatorio.");
        }
        if (expositor.getEmail() != null && !expositor.getEmail().isBlank()) {
            Expositor existente = expositorRepository.findByEmail(expositor.getEmail());
            if (existente != null) {
                throw new IllegalArgumentException(
                        "Ya existe un expositor registrado con el email: " + expositor.getEmail());
            }
        }
        return expositorRepository.save(expositor);
    }

    public Expositor buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del expositor es obligatorio.");
        }
        Expositor expositor = expositorRepository.findById(id);
        if (expositor == null) {
            throw new IllegalArgumentException("Expositor no encontrado con ID: " + id);
        }
        return expositor;
    }

    public List<Expositor> listarTodos() {
        return expositorRepository.findAll();
    }
}
