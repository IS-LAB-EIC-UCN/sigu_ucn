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
    private final List<Long> ids=Collections.synchronizedList(new ArrayList<>());
    private final List<Long> usuariosExtra=new ArrayList<>();
    private final List<Long> expositoresExtra=new ArrayList<>();
    private Categoria categoria;
    private Espacio sala;
    private Expositor expositor;
    private Usuario usuario;
    @BeforeEach void preparar() {
        usuario=new UsuarioRepository().findByCorreo("admin@sigu.cl");
        assertNotNull(usuario,"Se requiere el usuario demo admin@sigu.cl");
        try(EntityManager em=JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            categoria=new Categoria("Prueba-"+UUID.randomUUID());em.persist(categoria);
            sala=new Espacio("Prueba-"+UUID.randomUUID(),"EVENTOS",5,true); em.persist(sala);
            expositor=new Expositor();expositor.setNombre("Expositor de prueba");em.persist(expositor);
            em.getTransaction().commit();
        }
    }
    private Evento nuevo(int hora) {
        Evento e=new Evento(); e.setTitulo("Prueba automatizada");e.setTematica("Ciencia");e.setCategoria(categoria);e.setFecha(LocalDate.now().plusDays(10));
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
            for(Long id:usuariosExtra) em.createQuery("DELETE FROM Usuario WHERE id=:id").setParameter("id",id).executeUpdate();
            for(Long id:expositoresExtra) em.createQuery("DELETE FROM Expositor WHERE id=:id").setParameter("id",id).executeUpdate();
            if(expositor!=null) em.createQuery("DELETE FROM Expositor WHERE id=:id").setParameter("id",expositor.getId()).executeUpdate();
            if(categoria!=null) em.createQuery("DELETE FROM Categoria WHERE id=:id").setParameter("id",categoria.getId()).executeUpdate();
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
    @Test void editarSalaConservaEventosYProtegeCapacidad() {
        Evento e=nuevo(10);e.setCupoMaximo(4);guardar(e);
        CatalogoEventosRepository catalogo=new CatalogoEventosRepository();
        sala.setCapacidad(2);assertThrows(IllegalArgumentException.class,()->catalogo.actualizarEspacio(sala));
        assertEquals(5,catalogo.buscar(Espacio.class,sala.getId()).getCapacidad());
        sala.setCapacidad(4);sala.setNombre(sala.getNombre()+" editada");catalogo.actualizarEspacio(sala);
        assertEquals(sala.getNombre(),eventos.findById(e.getId()).getLugar());
    }
    @Test void editaExpositorSinPerderSuAsociacion() {
        Evento e=nuevo(10);guardar(e);expositor.setNombre("Nombre actualizado");expositor.setInstitucion("UCN");
        new CatalogoEventosRepository().guardar(expositor);
        assertEquals("Nombre actualizado",eventos.findById(e.getId()).getExpositor().getNombre());
        assertEquals("UCN",eventos.findById(e.getId()).getExpositor().getInstitucion());
    }
    @Test void persisteYReemplazaVariosExpositores() {
        Expositor otro=new Expositor();otro.setNombre("Segundo expositor");
        try(EntityManager em=JPAUtil.getEntityManager()) {
            em.getTransaction().begin();em.persist(otro);em.getTransaction().commit();
        }
        expositoresExtra.add(otro.getId());
        Evento e=nuevo(10);e.setExpositores(List.of(expositor,otro));guardar(e);
        assertEquals(2,eventos.findById(e.getId()).getExpositores().size());
        e.setExpositores(List.of(otro));new EventoService(eventos).guardar(e);
        assertEquals(1,eventos.findById(e.getId()).getExpositores().size());
        assertEquals(otro.getId(),eventos.findById(e.getId()).getExpositores().get(0).getId());
    }
    @Test void registraAsistenciaConPermisosYConservaElEstado() {
        Usuario docente=new UsuarioRepository().findByCorreo("docente@sigu.cl");
        Evento e=nuevo(10);e.setOrganizador(docente);guardar(e);new EventoService(eventos).publicar(e);inscripciones.inscribir(e.getId(),usuario.getId());
        Long id=inscripciones.listarPorEvento(e.getId()).get(0).getId();
        assertThrows(SecurityException.class,()->inscripciones.registrarAsistencia(e.getId(),id,usuario.getId(),"ESTUDIANTE",true));
        inscripciones.registrarAsistencia(e.getId(),id,docente.getId(),"DOCENTE",true);
        assertTrue(inscripciones.listarPorEvento(e.getId()).get(0).getAsistencia());
        assertEquals("INSCRITO",inscripciones.listarPorEvento(e.getId()).get(0).getEstado());
        inscripciones.cancelarInscripcion(e.getId(),usuario.getId());
        assertThrows(IllegalStateException.class,()->inscripciones.registrarAsistencia(e.getId(),id,docente.getId(),"DOCENTE",true));
        inscripciones.inscribir(e.getId(),usuario.getId());assertFalse(inscripciones.listarPorEvento(e.getId()).get(0).getAsistencia());
    }
    @Test void cancelacionFueraDePlazoNoModificaLaInscripcion() {
        Evento e=nuevo(10);guardar(e);new EventoService(eventos).publicar(e);inscripciones.inscribir(e.getId(),usuario.getId());
        Clock inicio=Clock.fixed(e.getFecha().atTime(e.getHoraInicio()).toInstant(ZoneOffset.UTC),ZoneOffset.UTC);
        assertThrows(IllegalStateException.class,()->new InscripcionRepository(inicio).cancelarInscripcion(e.getId(),usuario.getId()));
        assertTrue(inscripciones.estaInscrito(e.getId(),usuario.getId()));
    }
    @Test void administradorAjenoNoRegistraLaAsistenciaOficial() {
        Usuario docente=new UsuarioRepository().findByCorreo("docente@sigu.cl");
        Evento e=nuevo(10);e.setOrganizador(docente);guardar(e);new EventoService(eventos).publicar(e);
        inscripciones.inscribir(e.getId(),usuario.getId());Long id=inscripciones.listarPorEvento(e.getId()).get(0).getId();
        assertThrows(SecurityException.class,()->inscripciones.registrarAsistencia(e.getId(),id,usuario.getId(),"ADMIN",true));
        assertFalse(inscripciones.listarPorEvento(e.getId()).get(0).getAsistencia());
        inscripciones.registrarAsistencia(e.getId(),id,docente.getId(),"DOCENTE",true);
        assertTrue(inscripciones.listarPorEvento(e.getId()).get(0).getAsistencia());
    }
    private long competir(int cantidad, java.util.function.IntFunction<Boolean> accion) throws Exception {
        var pool=java.util.concurrent.Executors.newFixedThreadPool(cantidad);
        var listos=new java.util.concurrent.CountDownLatch(cantidad);
        var salida=new java.util.concurrent.CountDownLatch(1);
        var resultados=new ArrayList<java.util.concurrent.Future<Boolean>>();
        try {
            for(int i=0;i<cantidad;i++) {
                final int indice=i;
                resultados.add(pool.submit(()->{listos.countDown();salida.await();return accion.apply(indice);}));
            }
            assertTrue(listos.await(10,java.util.concurrent.TimeUnit.SECONDS));salida.countDown();
            long aceptados=0;
            for(var resultado:resultados) if(resultado.get(30,java.util.concurrent.TimeUnit.SECONDS)) aceptados++;
            return aceptados;
        } finally {
            salida.countDown();pool.shutdownNow();
            assertTrue(pool.awaitTermination(30,java.util.concurrent.TimeUnit.SECONDS));
        }
    }
    @Test void ochoSolicitudesDelMismoUsuarioCreanUnaSolaInscripcion() throws Exception {
        Evento e=nuevo(10);e.setCupoMaximo(5);guardar(e);new EventoService(eventos).publicar(e);
        long aceptados=competir(8,i->{
            try {inscripciones.inscribir(e.getId(),usuario.getId());return true;}
            catch(IllegalStateException esperado) {return false;}
        });
        assertEquals(1,aceptados);assertEquals(1,inscripciones.contarInscritos(e.getId()));
        assertEquals(1,inscripciones.listarPorEvento(e.getId()).size());
    }
    @Test void usuariosConcurrentesNoSuperanLosUltimosDosCupos() throws Exception {
        try(EntityManager em=JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            Rol rol=em.getReference(Rol.class,usuario.getRol().getId());
            for(int i=0;i<8;i++) {
                Usuario u=new Usuario("Prueba concurrente",UUID.randomUUID()+"@example.test",usuario.getPassword(),true,rol);
                em.persist(u);usuariosExtra.add(u.getId());
            }
            em.getTransaction().commit();
        }
        Evento e=nuevo(10);e.setCupoMaximo(2);guardar(e);new EventoService(eventos).publicar(e);
        long aceptados=competir(8,i->{
            try {inscripciones.inscribir(e.getId(),usuariosExtra.get(i));return true;}
            catch(IllegalStateException esperado) {return false;}
        });
        assertEquals(2,aceptados);assertEquals(2,inscripciones.contarInscritos(e.getId()));
    }
    @Test void eventosConcurrentesNoReservanLaMismaSalaYHorario() throws Exception {
        long aceptados=competir(4,i->{
            try {guardar(nuevo(10));return true;}
            catch(IllegalArgumentException esperado) {return false;}
        });
        assertEquals(1,aceptados);
        try(EntityManager em=JPAUtil.getEntityManager()) {
            assertEquals(1L,em.createQuery("SELECT COUNT(e) FROM Evento e WHERE e.espacio.id=:id",Long.class).setParameter("id",sala.getId()).getSingleResult());
        }
    }
    @Test void persisteCatalogosYEvento() {
        Evento e=nuevo(10);guardar(e);Evento leido=eventos.findById(e.getId());
        assertEquals(categoria.getNombre(),leido.getTematica());
        assertEquals(categoria.getId(),leido.getCategoria().getId());assertEquals(sala.getNombre(),leido.getEspacio().getNombre());
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
