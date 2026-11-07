package cl.ucn.app.service;

import cl.ucn.app.model.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BibliotecaService {
    Prestamo solicitarPrestamo(Long lectorId, Long ejemplarId, LocalDate fechaPrestamo);
    Prestamo registrarDevolucion(String codigoInventario, LocalDate fechaDevolucion, boolean ejemplarDanado, Long bibliotecarioId);
    Multa registrarPagoMulta(Long multaId, LocalDate fechaPago);
    List<Libro> buscarLibros(String termino);
    Libro registrarLibroConEjemplares(String isbn, String titulo, String editorial, int anio, Long categoriaId, List<Long> autoresIds, int cantidadEjemplares, String ubicacion);
    List<Prestamo> consultarHistorialLector(Long lectorId);
    List<Prestamo> consultarPrestamosActivos(Long lectorId);
    List<Multa> consultarMultasPendientes(Long lectorId);
    Optional<Bibliotecario> buscarBibliotecarioPorCorreo(String correo);
}
