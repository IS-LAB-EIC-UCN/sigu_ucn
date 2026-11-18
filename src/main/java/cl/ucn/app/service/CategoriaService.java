package cl.ucn.app.service;

import cl.ucn.app.model.Categoria;
import cl.ucn.app.repository.CategoriaRepository;

import java.util.List;
import java.util.Objects;

// reglas del catalogo de categorías de cafetería (RF02, CU09).
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService() {
        this(new CategoriaRepository());
    }

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = Objects.requireNonNull(categoriaRepository);
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria registrarCategoria(String nombre, String descripcion) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        String nombreNormalizado = nombre.strip();
        if (nombreNormalizado.length() > 100) {
            throw new IllegalArgumentException("El nombre de la categoría no puede superar los 100 caracteres.");
        }
        // El repositorio compara los nombres sin distinguir mayúsculas y minúsculas.
        if (categoriaRepository.findByNombre(nombreNormalizado) != null) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
        }

        Categoria categoria = new Categoria(nombreNormalizado,
                descripcion == null ? null : descripcion.strip());
        categoriaRepository.save(categoria);
        return categoria;
    }
}
