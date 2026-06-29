package cl.ucn.app.service.Interfaces;

import cl.ucn.app.model.Recurso;
import java.util.List;

public interface IInventario {
    List<Recurso> obtenerInventarioCompleto();
    List<Recurso> filtrarRecursosPorCategoria(String categoria);
}