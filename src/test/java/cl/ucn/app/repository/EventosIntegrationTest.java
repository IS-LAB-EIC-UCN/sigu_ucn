package cl.ucn.app.repository;
import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.*;
import cl.ucn.app.service.EventoService;
import jakarta.persistence.EntityManager;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import static org.junit.jupiter.api.Assertions.*;

/** Usa PostgreSQL real. Cada prueba crea y elimina solo sus propios registros. */
@EnabledIfSystemProperty(named="eventos.integration",matches="true")
class EventosIntegrationTest {
    private final EventoRepository eventos=new EventoRepository();
    private final InscripcionRepository inscripciones=new InscripcionRepository();
    private final List<Long> ids=new ArrayList<>();
    private Espacio sala;
    private Expositor expositor;
    private Usuario usuario;
    @BeforeEach void preparar() {
        usuario=new UsuarioRepository().findByCorreo("admin@sigu.cl");
        assertNotNull(usuario,"Se requiere el usuario demo admin@sigu.cl");
        try(EntityManager em=JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            sala=new Espacio("Prueba-"+UUID.randomUUID(),"EVENTOS",5,true); em.persist(sala);
            expositor=new Expositor();expositor.setNombre("Expositor de prueba");em.persist(expositor);
            em.getTransaction().commit();
        }
    }
    private Evento nuevo(int hora) {
        Evento e=new Evento(); e.setTitulo("Prueba automatizada");e.setTematica("Ciencia");e.setFecha(LocalDate.now().plusDays(10));
        e.setHoraInicio(LocalTime.of(hora,0));e.setHoraFin(LocalTime.of(hora+1,0));e.setCupoMaximo(1);
        e.setEspacio(sala);e.setExpositor(expositor);e.setOrganizador(usuario);
        return e;
    }
    private void guardar(Evento e) { new EventoService(eventos).guardar(e);ids.add(e.getId()); }
    @AfterEach void limpiar() {
        try(EntityManager em=JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            for(Long id:ids) {
                em.createQuery("DELETE FROM Inscripcion WHERE evento.id=:id").setParameter("id",id).executeUpdate();
                em.createQuery("DELETE FROM Evento WHERE id=:id").setParameter("id",id).executeUpdate();
            }
            if(expositor!=null) em.createQuery("DELETE FROM Expositor WHERE id=:id").setParameter("id",expositor.getId()).executeUpdate();
            if(sala!=null) em.createQuery("DELETE FROM Espacio WHERE id=:id").setParameter("id",sala.getId()).executeUpdate();
            em.getTransaction().commit();
        }
    }
    @Test void reactivarConservaHistorialYExigeRevisarAgenda() {
        Evento e=nuevo(10);guardar(e);new EventoService(eventos).publicar(e);
        inscripciones.inscribir(e.getId(),usuario.getId());new EventoService(eventos).cancelar(e);
        Evento reemplazo=nuevo(10);guardar(reemplazo);
        new EventoService(eventos).reactivar(e);
        assertEquals("BORRADOR",eventos.findById(e.getId()).getEstado());
        assertEquals(0,inscripciones.contarInscritos(e.getId()));
        assertEquals(1,inscripciones.listarPorEvento(e.getId()).size());
        assertEquals("CANCELADO",inscripciones.listarPorEvento(e.getId()).get(0).getEstado());
        assertThrows(IllegalArgumentException.class,()->eventos.reactivar(e.getId()));
        assertThrows(IllegalStateException.class,()->inscripciones.inscribir(e.getId(),usuario.getId()));
    }
    @Test void persisteCatalogosYEvento() {
        Evento e=nuevo(10);guardar(e);Evento leido=eventos.findById(e.getId());
        assertEquals("Ciencia",leido.getTematica());assertEquals(sala.getNombre(),leido.getEspacio().getNombre());
        assertEquals(expositor.getNombre(),leido.getExpositor().getNombre());
    }
    @Test void impideSolapamientoYPermiteHorarioConsecutivo() {
        guardar(nuevo(10));assertThrows(IllegalArgumentException.class,()->guardar(nuevo(10)));guardar(nuevo(11));
    }
    @Test void controlaInscripcionDuplicadaYReactivacion() {
        Evento e=nuevo(10);guardar(e);new EventoService(eventos).publicar(e);
        inscripciones.inscribir(e.getId(),usuario.getId());
        assertThrows(IllegalStateException.class,()->inscripciones.inscribir(e.getId(),usuario.getId()));
        assertEquals(1,inscripciones.contarInscritos(e.getId()));
        inscripciones.cancelarInscripcion(e.getId(),usuario.getId());assertEquals(0,inscripciones.contarInscritos(e.getId()));
        inscripciones.inscribir(e.getId(),usuario.getId());assertEquals(1,inscripciones.listarPorEvento(e.getId()).size());
    }
    @Test void bloqueaSobreCupoYEventosCancelados() {
        Evento e=nuevo(10);guardar(e);new EventoService(eventos).publicar(e);inscripciones.inscribir(e.getId(),usuario.getId());
        Usuario otro=new UsuarioRepository().findByCorreo("estudiante@sigu.cl");
        assertThrows(IllegalStateException.class,()->inscripciones.inscribir(e.getId(),otro.getId()));
        new EventoService(eventos).cancelar(e);
        assertThrows(IllegalStateException.class,()->inscripciones.inscribir(e.getId(),otro.getId()));
        assertEquals(1,inscripciones.resumenPorEvento(usuario.getId()).get(e.getId())[1]);
    }
}
