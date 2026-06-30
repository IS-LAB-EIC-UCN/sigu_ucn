package cl.ucn.app.integration;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Evento;
import cl.ucn.app.repository.EventoRepository;
import cl.ucn.app.service.EventoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class EventoIntegrationTest {

    private EventoService eventoService;
    private EventoRepository eventoRepository;

    @BeforeEach
    void setUp() {
        limpiarBaseDeDatos();
        eventoService = new EventoService();
        eventoRepository = new EventoRepository();
    }

    @AfterAll
    static void cerrarConexion() {
        JPAUtil.shutdown();
    }

    // TEST 1 DE INTEGRACIÓN
    @Test
    void registrarEventoDebeGuardarloEnBaseDeDatos() {
        Espacio espacio = crearEspacioDePrueba();

        Evento evento = new Evento(
                "Charla de IA",
                "Charla sobre inteligencia artificial",
                LocalDate.of(2026, 7, 10),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                "Tecnología",
                30,
                espacio
        );

        Evento guardado = eventoService.registrar(evento);

        assertNotNull(guardado.getId());

        Evento encontrado = eventoRepository.findById(guardado.getId());

        assertNotNull(encontrado);
        assertEquals("Charla de IA", encontrado.getTitulo());
        assertEquals("PLANIFICADO", encontrado.getEstado());
    }

    // TEST 2 DE INTEGRACIÓN
    @Test
    void noDebePermitirDosEventosEnMismoEspacioYHorario() {
        Espacio espacio = crearEspacioDePrueba();

        Evento evento1 = new Evento(
                "Seminario 1",
                "Primer evento",
                LocalDate.of(2026, 7, 10),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                "Tecnología",
                30,
                espacio
        );

        eventoService.registrar(evento1);

        Evento evento2 = new Evento(
                "Seminario 2",
                "Evento con horario repetido",
                LocalDate.of(2026, 7, 10),
                LocalTime.of(11, 0),
                LocalTime.of(13, 0),
                "Ciencia",
                20,
                espacio
        );

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> eventoService.registrar(evento2)
        );

        assertTrue(error.getMessage().contains("mismo espacio y horario"));
    }

    // TEST 3 DE INTEGRACIÓN
    @Test
    void cancelarEventoDebeActualizarEstadoEnBaseDeDatos() {
        Espacio espacio = crearEspacioDePrueba();

        Evento evento = new Evento(
                "Congreso UCN",
                "Evento académico",
                LocalDate.of(2026, 7, 15),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                "Académico",
                40,
                espacio
        );

        Evento guardado = eventoService.registrar(evento);

        eventoService.cancelar(guardado.getId());

        Evento actualizado = eventoRepository.findById(guardado.getId());

        assertEquals("CANCELADO", actualizado.getEstado());
    }

    private Espacio crearEspacioDePrueba() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Espacio espacio = new Espacio(
                    "Sala Test",
                    "SALA_CLASES",
                    50,
                    true
            );

            em.persist(espacio);
            em.getTransaction().commit();

            return espacio;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private void limpiarBaseDeDatos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            em.createNativeQuery("DELETE FROM evento_expositores").executeUpdate();
            em.createNativeQuery("DELETE FROM eventos").executeUpdate();
            em.createNativeQuery("DELETE FROM expositores").executeUpdate();
            em.createNativeQuery("DELETE FROM reservas").executeUpdate();
            em.createNativeQuery("DELETE FROM espacios").executeUpdate();

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
