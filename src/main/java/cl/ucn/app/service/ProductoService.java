package cl.ucn.app.service;

import cl.ucn.app.model.Categoria;
import cl.ucn.app.model.Producto;
import cl.ucn.app.repository.CategoriaRepository;
import cl.ucn.app.repository.ProductoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

// catalogo y stock de cafetería (RF01, RF10, RN05, CU01, CU05 y CU06)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService() {
        this(new ProductoRepository(), new CategoriaRepository());
    }

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = Objects.requireNonNull(productoRepository);
        this.categoriaRepository = Objects.requireNonNull(categoriaRepository);
    }

    //incluye productos con stock cero para consultar el inventario completo
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto registrarProducto(String nombre, BigDecimal precio, Integer stock, Long categoriaId) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        String nombreNormalizado = nombre.strip();
        if (nombreNormalizado.length() > 100) {
            throw new IllegalArgumentException("El nombre del producto no puede superar los 100 caracteres.");
        }
        if (precio == null || precio.signum() < 0) {
            throw new IllegalArgumentException("El precio debe ser igual o superior a cero.");
        }
        validarStock(stock);
        if (categoriaId == null || categoriaId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una categoría válida.");
        }
        Categoria categoria = categoriaRepository.findById(categoriaId);
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría seleccionada no existe.");
        }

        Producto producto = new Producto(nombreNormalizado, precio, stock, categoria);
        productoRepository.save(producto);
        return producto;
    }

    //fija la cantidad disponible:nuevoStock es el total, no una cantidad a sumar
    public Producto actualizarStock(Long productoId, Integer nuevoStock) {
        if (productoId == null || productoId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un producto válido.");
        }
        validarStock(nuevoStock);
        Producto producto = productoRepository.findById(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("El producto seleccionado no existe.");
        }

        producto.setStock(nuevoStock);
        productoRepository.update(producto);
        return producto;
    }

    private void validarStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock debe ser igual o superior a cero.");
        }
    }
}
