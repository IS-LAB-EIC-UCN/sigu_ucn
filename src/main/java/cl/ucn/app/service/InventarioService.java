package cl.ucn.app.service;

import cl.ucn.app.service.Interfaces.IInventario;
import cl.ucn.app.model.Recurso;
import cl.ucn.app.repository.InventarioRepository;
import java.util.ArrayList;
import java.util.List;

public class InventarioService implements IInventario {

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public List<Recurso> obtenerInventarioCompleto() {
        return inventarioRepository.findAll();
    }

    @Override
    public List<Recurso> filtrarRecursosPorCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            return new ArrayList<>();
        }

        return inventarioRepository.findByCategoria(categoria);
    }
}