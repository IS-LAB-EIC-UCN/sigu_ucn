package cl.ucn.app.service;

import cl.ucn.app.dto.NuevoProductoDTO;
import cl.ucn.app.exception.RecursoNoEncontradoException;
import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.model.Categoria;
import cl.ucn.app.model.Equipo;
import cl.ucn.app.model.Insumo;
import cl.ucn.app.model.Producto;
import cl.ucn.app.model.Proveedor;
import cl.ucn.app.repository.CategoriaRepository;
import cl.ucn.app.repository.ProductoRepository;
import cl.ucn.app.repository.ProveedorRepository;

import java.util.List;
import java.util.regex.Pattern;

/**
 * CU01 - Registrar productos e insumos (Grupo 5).
 * Depende solo de interfaces de repositorio (se inyectan por constructor).
 */
public class InventarioService {

    private static final Pattern FORMATO_CODIGO = Pattern.compile("[A-Za-z0-9-]{2,30}");

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;

    public InventarioService(ProductoRepository productoRepository,
                             CategoriaRepository categoriaRepository,
                             ProveedorRepository proveedorRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
    }

    public List<Categoria> categorias() {
        return categoriaRepository.listar();
    }

    public List<Proveedor> proveedores() {
        return proveedorRepository.listar();
    }

    /**
     * CU01: valida los datos, verifica que el código no exista y guarda el producto.
     */
    public Producto registrarProducto(NuevoProductoDTO datos) {
        String tipo = obligatorio(datos.tipo(), "tipo de recurso").toUpperCase();
        String codigo = obligatorio(datos.codigo(), "código").toUpperCase();
        String nombre = obligatorio(datos.nombre(), "nombre");
        String descripcion = datos.descripcion() == null ? null : datos.descripcion().trim();

        if (!FORMATO_CODIGO.matcher(codigo).matches()) {
            throw new ReglaNegocioException(
                    "El código debe tener entre 2 y 30 caracteres (letras, números o guion).");
        }
        if (nombre.length() > 100) {
            throw new ReglaNegocioException("El nombre no puede superar los 100 caracteres.");
        }
        if (descripcion != null && descripcion.length() > 255) {
            throw new ReglaNegocioException("La descripción no puede superar los 255 caracteres.");
        }
        if (datos.categoriaId() == null) {
            throw new ReglaNegocioException("Debe seleccionar una categoría.");
        }

        Categoria categoria = categoriaRepository.buscarPorId(datos.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("La categoría seleccionada no existe."));
        Proveedor proveedor = datos.proveedorId() == null ? null
                : proveedorRepository.buscarPorId(datos.proveedorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("El proveedor seleccionado no existe."));

        if (productoRepository.existeCodigo(codigo)) {
            throw new ReglaNegocioException("Ya existe un producto registrado con el código " + codigo + ".");
        }

        Producto producto = switch (tipo) {
            case "EQUIPO" -> new Equipo(codigo, nombre, descripcion, categoria, proveedor);
            case "INSUMO" -> {
                if (datos.stockMinimo() == null) {
                    throw new ReglaNegocioException("Debe indicar el stock mínimo del insumo.");
                }
                yield new Insumo(codigo, nombre, descripcion, categoria, proveedor, datos.stockMinimo());
            }
            default -> throw new ReglaNegocioException("El tipo de recurso debe ser EQUIPO o INSUMO.");
        };

        productoRepository.guardar(producto);
        return producto;
    }

    private static String obligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }
}