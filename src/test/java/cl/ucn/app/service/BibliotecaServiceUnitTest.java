package cl.ucn.app.service;

import cl.ucn.app.model.*;
import cl.ucn.app.repository.*;
import cl.ucn.app.util.IsbnValidator;
import cl.ucn.app.util.RutValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BibliotecaServiceUnitTest {

    @Mock private LibroRepository libroRepository;
    @Mock private EjemplarRepository ejemplarRepository;
    @Mock private PrestamoRepository prestamoRepository;
    @Mock private LectorRepository lectorRepository;
    @Mock private MultaRepository multaRepository;
    @Mock private BibliotecarioRepository bibliotecarioRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private AutorRepository autorRepository;

    private BibliotecaService bibliotecaService;

    @BeforeEach
    public void setUp() {
        bibliotecaService = new BibliotecaServiceImpl(
                libroRepository, ejemplarRepository, prestamoRepository,
                lectorRepository, multaRepository, bibliotecarioRepository,
                categoriaRepository, autorRepository
        );
    }

    // -------------------------------------------------------------
    // PRUEBAS DE SOLICITUD DE PRÉSTAMO (RN-01, RN-02, RN-07, RN-08)
    // -------------------------------------------------------------

    @Test
    @DisplayName("CP-01: Solicitar préstamo exitoso para lector habilitado y ejemplar disponible")
    void solicitarPrestamoExitoso() {
        Lector lector = new Lector("11.111.111-1", "Ana Estudiante", "ana@alumnos.ucn.cl", TipoLector.ESTUDIANTE);
        lector.setId(1L);

        Categoria cat = new Categoria("Informática");
        Libro libro = new Libro("978-956-00-1111-1", "Clean Code", "Editorial", 2020, cat);
        Ejemplar ejemplar = new Ejemplar("EJ-001", "Piso 1", libro);
        ejemplar.setId(10L);

        when(lectorRepository.findById(1L)).thenReturn(Optional.of(lector));
        when(ejemplarRepository.findById(10L)).thenReturn(Optional.of(ejemplar));
        when(prestamoRepository.findByLectorIdAndEstado(1L, EstadoPrestamo.ACTIVO)).thenReturn(Collections.emptyList());
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalDate hoy = LocalDate.now();
        Prestamo prestamo = bibliotecaService.solicitarPrestamo(1L, 10L, hoy);

        assertNotNull(prestamo);
        assertEquals(EstadoEjemplar.PRESTADO, ejemplar.getEstado());
        assertEquals(hoy.plusDays(TipoLector.ESTUDIANTE.getDiasPlazo()), prestamo.getFechaLimite());
        verify(ejemplarRepository).update(ejemplar);
        verify(prestamoRepository).save(any(Prestamo.class));
    }

    @Test
    @DisplayName("CP-02: Rechazar préstamo cuando el lector está bloqueado (RN-02)")
    void rechazarPrestamoLectorBloqueado() {
        Lector lector = new Lector("11.111.111-1", "Juan Bloqueado", "juan@ucn.cl", TipoLector.DOCENTE);
        lector.setId(2L);
        lector.bloquear();

        when(lectorRepository.findById(2L)).thenReturn(Optional.of(lector));

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () ->
                bibliotecaService.solicitarPrestamo(2L, 10L, LocalDate.now())
        );
        assertEquals("RN-02", ex.getCodigoRegla());
        verify(prestamoRepository, never()).save(any());
    }

    @Test
    @DisplayName("CP-03: Rechazar préstamo cuando el ejemplar ya está prestado (RN-01)")
    void rechazarPrestamoEjemplarNoDisponible() {
        Lector lector = new Lector("11.111.111-1", "Carlos Lector", "carlos@ucn.cl", TipoLector.ESTUDIANTE);
        lector.setId(1L);

        Categoria cat = new Categoria("Informática");
        Libro libro = new Libro("978-956-00-1111-1", "Arquitectura", "Editorial", 2021, cat);
        Ejemplar ejemplar = new Ejemplar("EJ-002", "Piso 2", libro);
        ejemplar.setId(20L);
        ejemplar.marcarPrestado();

        when(lectorRepository.findById(1L)).thenReturn(Optional.of(lector));
        when(prestamoRepository.findByLectorIdAndEstado(1L, EstadoPrestamo.ACTIVO)).thenReturn(Collections.emptyList());
        when(ejemplarRepository.findById(20L)).thenReturn(Optional.of(ejemplar));

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () ->
                bibliotecaService.solicitarPrestamo(1L, 20L, LocalDate.now())
        );
        assertEquals("RN-01", ex.getCodigoRegla());
    }

    @Test
    @DisplayName("CP-04: Rechazar préstamo si el lector tiene un préstamo vencido no devuelto (RN-07)")
    void rechazarPrestamoLectorConPrestamoVencido() {
        Lector lector = new Lector("11.111.111-1", "Maria Atrasada", "maria@ucn.cl", TipoLector.ESTUDIANTE);
        lector.setId(1L);

        Categoria cat = new Categoria("Matemáticas");
        Libro libro = new Libro("978-956-00-2222-2", "Cálculo", "Editorial", 2019, cat);
        Ejemplar ejVencido = new Ejemplar("EJ-VENCIDO", "Piso 1", libro);
        ejVencido.setId(99L);

        LocalDate hoy = LocalDate.now();
        Prestamo pVencido = new Prestamo(lector, ejVencido, hoy.minusDays(10), hoy.minusDays(3));

        when(lectorRepository.findById(1L)).thenReturn(Optional.of(lector));
        when(prestamoRepository.findByLectorIdAndEstado(1L, EstadoPrestamo.ACTIVO))
                .thenReturn(Collections.singletonList(pVencido));

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () ->
                bibliotecaService.solicitarPrestamo(1L, 30L, hoy)
        );
        assertEquals("RN-07", ex.getCodigoRegla());
    }

    @Test
    @DisplayName("CP-05: Rechazar préstamo cuando el lector alcanza el cupo máximo permitido (RN-08)")
    void rechazarPrestamoExcedeCupoMaximo() {
        Lector lector = new Lector("11.111.111-1", "Pedro Lector", "pedro@ucn.cl", TipoLector.ESTUDIANTE);
        lector.setId(1L);

        Categoria cat = new Categoria("Química");
        Libro libro = new Libro("978-956-00-3333-3", "Química", "Editorial", 2020, cat);

        LocalDate hoy = LocalDate.now();
        List<Prestamo> tresPrestamos = List.of(
                new Prestamo(lector, new Ejemplar("E1", "Piso 1", libro), hoy, hoy.plusDays(7)),
                new Prestamo(lector, new Ejemplar("E2", "Piso 1", libro), hoy, hoy.plusDays(7)),
                new Prestamo(lector, new Ejemplar("E3", "Piso 1", libro), hoy, hoy.plusDays(7))
        );

        when(lectorRepository.findById(1L)).thenReturn(Optional.of(lector));
        when(prestamoRepository.findByLectorIdAndEstado(1L, EstadoPrestamo.ACTIVO)).thenReturn(tresPrestamos);

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () ->
                bibliotecaService.solicitarPrestamo(1L, 40L, hoy)
        );
        assertEquals("RN-08", ex.getCodigoRegla());
    }

    // -------------------------------------------------------------
    // PRUEBAS DE DEVOLUCIÓN Y MULTAS (RN-03, RN-04, RN-05, RN-06)
    // -------------------------------------------------------------

    @Test
    @DisplayName("CP-06: Registrar devolución a tiempo deja ejemplar disponible y no genera multa")
    void registrarDevolucionATiempo() {
        Lector lector = new Lector("11.111.111-1", "Laura", "laura@ucn.cl", TipoLector.ESTUDIANTE);
        lector.setId(1L);

        Categoria cat = new Categoria("Historia");
        Libro libro = new Libro("978-956-00-4444-4", "Historia", "Editorial", 2018, cat);
        Ejemplar ejemplar = new Ejemplar("EJ-HIST-01", "Piso 3", libro);
        ejemplar.setId(10L);
        ejemplar.marcarPrestado();

        LocalDate fechaPrestamo = LocalDate.of(2026, 5, 1);
        LocalDate fechaLimite = LocalDate.of(2026, 5, 8);
        Prestamo prestamo = new Prestamo(lector, ejemplar, fechaPrestamo, fechaLimite);
        prestamo.setId(50L);

        when(ejemplarRepository.findByCodigoInventario("EJ-HIST-01")).thenReturn(Optional.of(ejemplar));
        when(prestamoRepository.findActivoPorEjemplarId(ejemplar.getId())).thenReturn(Optional.of(prestamo));

        LocalDate fechaDevolucion = LocalDate.of(2026, 5, 5);
        Prestamo devuelto = bibliotecaService.registrarDevolucion("EJ-HIST-01", fechaDevolucion, false, null);

        assertEquals(EstadoPrestamo.DEVUELTO, devuelto.getEstado());
        assertEquals(EstadoEjemplar.DISPONIBLE, ejemplar.getEstado());
        assertNull(devuelto.getMulta());
        verify(multaRepository, never()).save(any());
        verify(prestamoRepository).update(prestamo);
    }

    @Test
    @DisplayName("CP-07: Devolución tardía genera multa por días de atraso y bloquea al lector (RN-03)")
    void registrarDevolucionTardiaGeneraMultaYBloqueo() {
        Lector lector = new Lector("11.111.111-1", "Diego", "diego@ucn.cl", TipoLector.ESTUDIANTE);
        lector.setId(1L);

        Categoria cat = new Categoria("Física");
        Libro libro = new Libro("978-956-00-5555-5", "Física Moderna", "Editorial", 2022, cat);
        Ejemplar ejemplar = new Ejemplar("EJ-FIS-01", "Piso 2", libro);
        ejemplar.setId(15L);
        ejemplar.marcarPrestado();

        LocalDate fechaPrestamo = LocalDate.of(2026, 5, 1);
        LocalDate fechaLimite = LocalDate.of(2026, 5, 8);
        Prestamo prestamo = new Prestamo(lector, ejemplar, fechaPrestamo, fechaLimite);
        prestamo.setId(60L);

        when(ejemplarRepository.findByCodigoInventario("EJ-FIS-01")).thenReturn(Optional.of(ejemplar));
        when(prestamoRepository.findActivoPorEjemplarId(ejemplar.getId())).thenReturn(Optional.of(prestamo));

        LocalDate fechaDevolucion = LocalDate.of(2026, 5, 11); // 3 días de atraso
        Prestamo devuelto = bibliotecaService.registrarDevolucion("EJ-FIS-01", fechaDevolucion, false, null);

        assertNotNull(devuelto.getMulta());
        assertEquals(3, devuelto.getMulta().getDiasAtraso());
        assertEquals(1500L, devuelto.getMulta().getMonto()); // 3 * 500
        assertEquals(EstadoLector.BLOQUEADO, lector.getEstado());

        verify(multaRepository).save(any(Multa.class));
        verify(lectorRepository).update(lector);
    }

    @Test
    @DisplayName("CP-08: Devolución con ejemplar dañado marca estado en reparación")
    void registrarDevolucionEjemplarDanado() {
        Lector lector = new Lector("11.111.111-1", "Sonia", "sonia@ucn.cl", TipoLector.DOCENTE);
        lector.setId(1L);

        Categoria cat = new Categoria("Arte");
        Libro libro = new Libro("978-956-00-6666-6", "Arte Moderno", "Editorial", 2021, cat);
        Ejemplar ejemplar = new Ejemplar("EJ-ARTE-01", "Piso 1", libro);
        ejemplar.setId(25L);
        ejemplar.marcarPrestado();

        LocalDate fecha = LocalDate.now();
        Prestamo prestamo = new Prestamo(lector, ejemplar, fecha.minusDays(2), fecha.plusDays(10));
        prestamo.setId(70L);

        when(ejemplarRepository.findByCodigoInventario("EJ-ARTE-01")).thenReturn(Optional.of(ejemplar));
        when(prestamoRepository.findActivoPorEjemplarId(ejemplar.getId())).thenReturn(Optional.of(prestamo));

        bibliotecaService.registrarDevolucion("EJ-ARTE-01", fecha, true, null);

        assertEquals(EstadoEjemplar.EN_REPARACION, ejemplar.getEstado());
        verify(ejemplarRepository).update(ejemplar);
    }

    @Test
    @DisplayName("CP-09: Registrar pago de multa rehabilita al lector si no tiene otras deudas")
    void registrarPagoMultaHabilitaLector() {
        Lector lector = new Lector("11.111.111-1", "Andrés", "andres@ucn.cl", TipoLector.ESTUDIANTE);
        lector.setId(1L);
        lector.bloquear();

        Categoria cat = new Categoria("Biología");
        Libro libro = new Libro("978-956-00-7777-7", "Biología Celular", "Editorial", 2020, cat);
        Ejemplar ej = new Ejemplar("EJ-BIO-01", "Piso 1", libro);
        ej.setId(30L);

        Prestamo prestamo = new Prestamo(lector, ej, LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        prestamo.setId(80L);
        Multa multa = new Multa(prestamo, 5, 2500L, LocalDate.now());
        multa.setId(100L);

        when(multaRepository.findById(100L)).thenReturn(Optional.of(multa));
        when(multaRepository.findByLectorIdAndEstado(lector.getId(), EstadoMulta.PENDIENTE))
                .thenReturn(Collections.emptyList());

        Multa pagada = bibliotecaService.registrarPagoMulta(100L, LocalDate.now());

        assertEquals(EstadoMulta.PAGADA, pagada.getEstado());
        assertEquals(EstadoLector.HABILITADO, lector.getEstado());
        verify(lectorRepository).update(lector);
    }

    // -------------------------------------------------------------
    // PRUEBAS DE REGISTRO Y VALIDACIÓN DE LIBROS E ISBN
    // -------------------------------------------------------------

    @Test
    @DisplayName("CP-10: Rechazar registro de libro con ISBN no válido")
    void registrarLibroIsbnInvalido() {
        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class, () ->
                bibliotecaService.registrarLibroConEjemplares(
                        "123-INVALIDO", "Libro Test", "Editorial", 2023,
                        1L, List.of(1L), 2, "Piso 1"
                )
        );
        assertEquals("isbn", ex.getCampo());
        verify(libroRepository, never()).save(any());
    }

    @Test
    @DisplayName("CP-11: Rechazar registro de libro con ISBN duplicado (RN-10)")
    void registrarLibroIsbnDuplicado() {
        String isbn = "978-0-13-214301-1";
        Categoria cat = new Categoria("Filosofía");
        cat.setId(1L);
        Libro existente = new Libro(IsbnValidator.normalizar(isbn), "Existente", "Ed", 2019, cat);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(libroRepository.findByIsbn(IsbnValidator.normalizar(isbn))).thenReturn(Optional.of(existente));

        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () ->
                bibliotecaService.registrarLibroConEjemplares(
                        isbn, "Nuevo Libro", "Ed", 2024,
                        1L, List.of(1L), 1, "Piso 1"
                )
        );
        assertEquals("RN-10", ex.getCodigoRegla());
    }

    @Test
    @DisplayName("CP-12: Validar algoritmo de RUT chileno con Módulo 11 (RutValidator)")
    void validarRutChileno() {
        assertTrue(RutValidator.esValido("11.111.111-1"));
        assertTrue(RutValidator.esValido("21.653.611-8"));
        assertTrue(RutValidator.esValido("216536118"));

        assertFalse(RutValidator.esValido("11.111.111-9")); // DV incorrecto
        assertFalse(RutValidator.esValido("abc"));
        assertFalse(RutValidator.esValido(""));
        assertFalse(RutValidator.esValido(null));
    }
}
