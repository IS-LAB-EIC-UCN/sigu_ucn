package cl.ucn.app.service;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.*;
import cl.ucn.app.repository.*;
import cl.ucn.app.util.IsbnValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BibliotecaServiceImpl implements BibliotecaService {

    public static final long MONTO_MULTA_DIARIO = 500L;

    private final LibroRepository libroRepository;
    private final EjemplarRepository ejemplarRepository;
    private final PrestamoRepository prestamoRepository;
    private final LectorRepository lectorRepository;
    private final MultaRepository multaRepository;
    private final BibliotecarioRepository bibliotecarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final AutorRepository autorRepository;

    public BibliotecaServiceImpl(LibroRepository libroRepository, EjemplarRepository ejemplarRepository,
                                  PrestamoRepository prestamoRepository, LectorRepository lectorRepository,
                                  MultaRepository multaRepository, BibliotecarioRepository bibliotecarioRepository,
                                  CategoriaRepository categoriaRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.prestamoRepository = prestamoRepository;
        this.lectorRepository = lectorRepository;
        this.multaRepository = multaRepository;
        this.bibliotecarioRepository = bibliotecarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.autorRepository = autorRepository;
    }

    @Override
    public Prestamo solicitarPrestamo(Long lectorId, Long ejemplarId, LocalDate fechaPrestamo) {
        return JPAUtil.runInTransaction(() -> {
            Lector lector = lectorRepository.findById(lectorId)
                    .orElseThrow(() -> new DatosInvalidosException("lectorId", "Lector no encontrado."));
            validarAptitudLector(lector, fechaPrestamo);

            Ejemplar ejemplar = ejemplarRepository.findById(ejemplarId)
                    .orElseThrow(() -> new DatosInvalidosException("ejemplarId", "Ejemplar no encontrado."));
            validarDisponibilidadEjemplar(ejemplar);

            LocalDate fechaLimite = fechaPrestamo.plusDays(lector.getTipo().getDiasPlazo());
            Prestamo nuevoPrestamo = new Prestamo(lector, ejemplar, fechaPrestamo, fechaLimite);
            ejemplar.marcarPrestado();
            ejemplarRepository.update(ejemplar);
            return prestamoRepository.save(nuevoPrestamo);
        });
    }

    private void validarAptitudLector(Lector lector, LocalDate fechaActual) {
        if (!lector.estaHabilitado()) {
            throw new ReglaNegocioException("RN-02", "Un lector BLOQUEADO no puede solicitar préstamos.");
        }
        List<Prestamo> activos = prestamoRepository.findByLectorIdAndEstado(lector.getId(), EstadoPrestamo.ACTIVO);
        for (Prestamo p : activos) {
            if (p.estaVencido(fechaActual)) {
                throw new ReglaNegocioException("RN-07", "El lector tiene un préstamo vencido sin devolver: " + p.getEjemplar().getCodigoInventario());
            }
        }
        if (activos.size() >= lector.getTipo().getMaxPrestamos()) {
            throw new ReglaNegocioException("RN-08", "El lector ha alcanzado el límite máximo de préstamos activos (" + lector.getTipo().getMaxPrestamos() + ").");
        }
    }

    private void validarDisponibilidadEjemplar(Ejemplar ejemplar) {
        if (ejemplar.getEstado() == EstadoEjemplar.EN_REPARACION || ejemplar.getEstado() == EstadoEjemplar.DADO_DE_BAJA) {
            throw new ReglaNegocioException("RN-09", "Un ejemplar en reparación o dado de baja no se puede prestar.");
        }
        if (!ejemplar.estaDisponible()) {
            throw new ReglaNegocioException("RN-01", "El ejemplar ya está prestado y no está disponible.");
        }
    }

    @Override
    public Prestamo registrarDevolucion(String codigoInventario, LocalDate fechaDevolucion, boolean ejemplarDanado, Long bibliotecarioId) {
        return JPAUtil.runInTransaction(() -> {
            Ejemplar ejemplar = ejemplarRepository.findByCodigoInventario(codigoInventario)
                    .orElseThrow(() -> new DatosInvalidosException("codigoInventario", "El código de inventario no existe."));
            Prestamo prestamo = prestamoRepository.findActivoPorEjemplarId(ejemplar.getId())
                    .orElseThrow(() -> new ReglaNegocioException("RN-01", "El ejemplar no tiene un préstamo activo asociado."));

            if (bibliotecarioId != null) {
                bibliotecarioRepository.findById(bibliotecarioId).ifPresent(prestamo::setRecibidoPor);
            }
            prestamo.registrarDevolucion(fechaDevolucion);
            actualizarEstadoEjemplarDevuelto(ejemplar, ejemplarDanado);
            aplicarMultaSiHayAtraso(prestamo, fechaDevolucion);

            prestamoRepository.update(prestamo);
            return prestamo;
        });
    }

    private void actualizarEstadoEjemplarDevuelto(Ejemplar ejemplar, boolean ejemplarDanado) {
        if (ejemplarDanado) {
            ejemplar.marcarEnReparacion();
        } else {
            ejemplar.marcarDisponible();
        }
        ejemplarRepository.update(ejemplar);
    }

    private void aplicarMultaSiHayAtraso(Prestamo prestamo, LocalDate fechaDevolucion) {
        int diasAtraso = prestamo.calcularDiasAtraso();
        if (diasAtraso > 0) {
            long monto = diasAtraso * MONTO_MULTA_DIARIO;
            Multa multa = new Multa(prestamo, diasAtraso, monto, fechaDevolucion);
            multaRepository.save(multa);
            prestamo.setMulta(multa);

            Lector lector = prestamo.getLector();
            lector.bloquear();
            lectorRepository.update(lector);
        }
    }

    @Override
    public Multa registrarPagoMulta(Long multaId, LocalDate fechaPago) {
        return JPAUtil.runInTransaction(() -> {
            Multa multa = multaRepository.findById(multaId)
                    .orElseThrow(() -> new DatosInvalidosException("multaId", "Multa no encontrada."));
            if (!multa.estaPendiente()) {
                throw new ReglaNegocioException("RN-06", "La multa ya se encuentra pagada.");
            }
            multa.pagar(fechaPago);
            multaRepository.update(multa);

            Lector lector = multa.getPrestamo().getLector();
            List<Multa> pendientes = multaRepository.findByLectorIdAndEstado(lector.getId(), EstadoMulta.PENDIENTE);
            if (pendientes.isEmpty()) {
                lector.habilitar();
                lectorRepository.update(lector);
            }
            return multa;
        });
    }

    @Override
    public List<Libro> buscarLibros(String termino) {
        return libroRepository.buscarPorCriterio(termino);
    }

    @Override
    public Libro registrarLibroConEjemplares(String isbn, String titulo, String editorial, int anio,
                                             Long categoriaId, List<Long> autoresIds, int cantidadEjemplares, String ubicacion) {
        return JPAUtil.runInTransaction(() -> {
            validarDatosNuevoLibro(isbn, titulo, categoriaId, autoresIds, cantidadEjemplares);
            Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new DatosInvalidosException("categoriaId", "Categoría no encontrada."));

            String isbnNormalizado = IsbnValidator.normalizar(isbn);
            Libro libro = new Libro(isbnNormalizado, titulo.trim(), editorial == null ? "" : editorial.trim(), anio, categoria);
            for (Long autorId : autoresIds) {
                autorRepository.findById(autorId).ifPresent(libro.getAutores()::add);
            }
            generarEjemplaresIniciales(libro, isbnNormalizado, cantidadEjemplares, ubicacion);
            return libroRepository.save(libro);
        });
    }

    private void validarDatosNuevoLibro(String isbn, String titulo, Long categoriaId, List<Long> autoresIds, int cantidadEjemplares) {
        if (!IsbnValidator.esValido(isbn)) {
            throw new DatosInvalidosException("isbn", "El ISBN no tiene un formato válido (10 o 13 dígitos).");
        }
        if (titulo == null || titulo.trim().isBlank()) {
            throw new DatosInvalidosException("titulo", "El título del libro es obligatorio.");
        }
        if (cantidadEjemplares <= 0) {
            throw new DatosInvalidosException("cantidadEjemplares", "Debe registrar al menos 1 ejemplar.");
        }
        if (autoresIds == null || autoresIds.isEmpty()) {
            throw new ReglaNegocioException("RN-11", "Todo libro debe tener al menos un autor.");
        }
        if (categoriaId == null) {
            throw new ReglaNegocioException("RN-11", "Todo libro debe tener exactamente una categoría.");
        }
        if (libroRepository.findByIsbn(IsbnValidator.normalizar(isbn)).isPresent()) {
            throw new ReglaNegocioException("RN-10", "Ya existe un libro registrado con este ISBN.");
        }
    }

    private void generarEjemplaresIniciales(Libro libro, String isbn, int cantidad, String ubicacion) {
        String baseCodigo = "EJ-" + isbn;
        String ubicacionFinal = (ubicacion == null || ubicacion.isBlank()) ? "General" : ubicacion.trim();
        for (int i = 1; i <= cantidad; i++) {
            String codigo = baseCodigo + "-" + String.format("%03d", i);
            libro.agregarEjemplar(new Ejemplar(codigo, ubicacionFinal, libro));
        }
    }

    @Override
    public List<Prestamo> consultarHistorialLector(Long lectorId) {
        return prestamoRepository.findByLectorId(lectorId);
    }

    @Override
    public List<Prestamo> consultarPrestamosActivos(Long lectorId) {
        return prestamoRepository.findByLectorIdAndEstado(lectorId, EstadoPrestamo.ACTIVO);
    }

    @Override
    public List<Multa> consultarMultasPendientes(Long lectorId) {
        return multaRepository.findByLectorIdAndEstado(lectorId, EstadoMulta.PENDIENTE);
    }

    @Override
    public Optional<Bibliotecario> buscarBibliotecarioPorCorreo(String correo) {
        return bibliotecarioRepository.findByCorreo(correo);
    }

    @Override
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    @Override
    public List<Autor> listarAutores() {
        return autorRepository.findAll();
    }
}
