package cl.ucn.app.integration;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.*;
import cl.ucn.app.repository.*;
import cl.ucn.app.service.BibliotecaService;
import cl.ucn.app.service.BibliotecaServiceImpl;
import cl.ucn.app.service.LectorService;
import cl.ucn.app.service.LectorServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BibliotecaIntegrationTest {

    private static BibliotecaService bibliotecaService;
    private static LectorService lectorService;
    private static CategoriaRepository categoriaRepository;
    private static AutorRepository autorRepository;
    private static EjemplarRepository ejemplarRepository;
    private static PrestamoRepository prestamoRepository;
    private static MultaRepository multaRepository;
    private static LectorRepository lectorRepository;

    @BeforeAll
    static void init() {
        categoriaRepository = new CategoriaRepositoryImpl();
        autorRepository = new AutorRepositoryImpl();
        ejemplarRepository = new EjemplarRepositoryImpl();
        prestamoRepository = new PrestamoRepositoryImpl();
        multaRepository = new MultaRepositoryImpl();
        lectorRepository = new LectorRepositoryImpl();
        BibliotecarioRepository bibliotecarioRepository = new BibliotecarioRepositoryImpl();
        LibroRepository libroRepository = new LibroRepositoryImpl();

        bibliotecaService = new BibliotecaServiceImpl(
                libroRepository, ejemplarRepository, prestamoRepository,
                lectorRepository, multaRepository, bibliotecarioRepository,
                categoriaRepository, autorRepository
        );
        lectorService = new LectorServiceImpl(lectorRepository);
    }

    @Test
    @Order(1)
    @DisplayName("IT-01: Persistencia integral de Lector, Categoría, Autor y Libro con Ejemplares")
    void testPersistenciaEntidadesBase() {
        Lector lector = lectorService.registrarLector(
                "21.653.611-8", "Enzo Diaz", "enzo.diaz@alumnos.ucn.cl", TipoLector.ESTUDIANTE
        );
        assertNotNull(lector.getId());
        assertEquals(EstadoLector.HABILITADO, lector.getEstado());

        Categoria cat = new Categoria("Sistemas Distribuidos");
        cat = categoriaRepository.save(cat);
        assertNotNull(cat.getId());

        Autor autor = new Autor("Andrew Tanenbaum");
        autor = autorRepository.save(autor);
        assertNotNull(autor.getId());

        Libro libro = bibliotecaService.registrarLibroConEjemplares(
                "978-013-214-301-1", "Distributed Systems", "Pearson", 2023,
                cat.getId(), List.of(autor.getId()), 2, "Estante B-12"
        );

        assertNotNull(libro.getId());
        assertEquals(2, libro.getEjemplares().size());
        assertEquals(EstadoEjemplar.DISPONIBLE, libro.getEjemplares().get(0).getEstado());
    }

    @Test
    @Order(2)
    @DisplayName("IT-02: Flujo transaccional de Préstamo y Devolución a tiempo")
    void testFlujoPrestamoYDevolucionATiempo() {
        Lector lector = lectorService.buscarPorRut("21.653.611-8").orElseThrow();
        List<Libro> libros = bibliotecaService.buscarLibros("Distributed Systems");
        assertFalse(libros.isEmpty());

        Ejemplar ejemplar = libros.get(0).getEjemplares().get(0);
        LocalDate fechaPrestamo = LocalDate.now();

        Prestamo prestamo = bibliotecaService.solicitarPrestamo(lector.getId(), ejemplar.getId(), fechaPrestamo);
        assertNotNull(prestamo.getId());
        assertEquals(EstadoPrestamo.ACTIVO, prestamo.getEstado());

        // Verificar cambio de estado en la base de datos
        Ejemplar ejemplarActualizado = ejemplarRepository.findById(ejemplar.getId()).orElseThrow();
        assertEquals(EstadoEjemplar.PRESTADO, ejemplarActualizado.getEstado());

        // Devolución en fecha correcta
        Prestamo devuelto = bibliotecaService.registrarDevolucion(
                ejemplar.getCodigoInventario(), fechaPrestamo.plusDays(2), false, null
        );

        assertEquals(EstadoPrestamo.DEVUELTO, devuelto.getEstado());
        assertNull(devuelto.getMulta());

        Ejemplar ejemplarDevuelto = ejemplarRepository.findById(ejemplar.getId()).orElseThrow();
        assertEquals(EstadoEjemplar.DISPONIBLE, ejemplarDevuelto.getEstado());
    }

    @Test
    @Order(3)
    @DisplayName("IT-03: Flujo transaccional de Devolución tardía, Generación de Multa y Bloqueo de Lector")
    void testFlujoDevolucionTardiaGeneraMultaYBloqueo() {
        Lector lector = lectorService.buscarPorRut("21.653.611-8").orElseThrow();
        List<Libro> libros = bibliotecaService.buscarLibros("Distributed Systems");
        Ejemplar segundoEjemplar = libros.get(0).getEjemplares().get(1);

        LocalDate fechaPasada = LocalDate.now().minusDays(15);
        Prestamo prestamo = bibliotecaService.solicitarPrestamo(lector.getId(), segundoEjemplar.getId(), fechaPasada);
        assertNotNull(prestamo.getId());

        // Devolver hoy (ha pasado más del plazo permitido de 7 días para estudiante)
        LocalDate fechaHoy = LocalDate.now();
        Prestamo devuelto = bibliotecaService.registrarDevolucion(
                segundoEjemplar.getCodigoInventario(), fechaHoy, false, null
        );

        assertNotNull(devuelto.getMulta());
        assertTrue(devuelto.getMulta().getDiasAtraso() > 0);
        assertTrue(devuelto.getMulta().getMonto() > 0);
        assertEquals(EstadoMulta.PENDIENTE, devuelto.getMulta().getEstado());

        // Verificar que el lector quedó BLOQUEADO en la base de datos
        Lector lectorEnBd = lectorRepository.findById(lector.getId()).orElseThrow();
        assertEquals(EstadoLector.BLOQUEADO, lectorEnBd.getEstado());
    }

    @Test
    @Order(4)
    @DisplayName("IT-04: Pago de Multa persistente y Reactivación de Lector")
    void testPagoDeMultaYReactivacion() {
        Lector lector = lectorService.buscarPorRut("21.653.611-8").orElseThrow();
        List<Multa> multas = bibliotecaService.consultarMultasPendientes(lector.getId());
        assertFalse(multas.isEmpty());

        Multa multa = multas.get(0);
        Multa pagada = bibliotecaService.registrarPagoMulta(multa.getId(), LocalDate.now());
        assertEquals(EstadoMulta.PAGADA, pagada.getEstado());

        // Verificar que el lector vuelve a estar HABILITADO
        Lector lectorRehabilitado = lectorRepository.findById(lector.getId()).orElseThrow();
        assertEquals(EstadoLector.HABILITADO, lectorRehabilitado.getEstado());
    }

    @Test
    @Order(5)
    @DisplayName("IT-05: Búsqueda avanzada de catálogo por título, autor o categoría")
    void testBusquedaAvanzadaCatalogo() {
        List<Libro> porTitulo = bibliotecaService.buscarLibros("Distributed");
        assertFalse(porTitulo.isEmpty());

        List<Libro> porAutor = bibliotecaService.buscarLibros("Tanenbaum");
        assertFalse(porAutor.isEmpty());

        List<Libro> porCategoria = bibliotecaService.buscarLibros("Sistemas");
        assertFalse(porCategoria.isEmpty());
    }
}
