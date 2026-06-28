package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Inscripcion;
import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Taller;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.InscripcionRepository;
import cl.ucn.app.repository.ITallerRepository;
import cl.ucn.app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TallerServiceTest {

    private FakeTallerRepository tallerRepo;
    private FakeInscripcionRepository inscripcionRepo;
    private FakeUsuarioRepository usuarioRepo;
    private FakeEspacioRepository espacioRepo;

    private TallerServiceImpl tallerService;

    private Usuario estudiante;
    private Usuario profesor;
    private Taller tallerBasico;
    private Espacio espacioVacio;

    private void setEntityId(Object entity, Long id) {
        try {
            java.lang.reflect.Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        tallerRepo = new FakeTallerRepository();
        inscripcionRepo = new FakeInscripcionRepository();
        usuarioRepo = new FakeUsuarioRepository();
        espacioRepo = new FakeEspacioRepository();

        tallerService = new TallerServiceImpl(tallerRepo, inscripcionRepo, usuarioRepo, espacioRepo);

        Rol rolEstudiante = new Rol();
        rolEstudiante.setNombre("ESTUDIANTE");

        Rol rolDocente = new Rol();
        rolDocente.setNombre("DOCENTE");

        estudiante = new Usuario();
        setEntityId(estudiante, 2L);
        estudiante.setRol(rolEstudiante);

        profesor = new Usuario();
        setEntityId(profesor, 5L);
        profesor.setRol(rolDocente);

        espacioVacio = new Espacio();
        setEntityId(espacioVacio, 1L);

        tallerBasico = new Taller();
        setEntityId(tallerBasico, 1L);
        tallerBasico.setCuposTotales(10);
        tallerBasico.setEstado("ABIERTO");
        tallerBasico.setProfesor(profesor);
        tallerBasico.setBloqueHorario('A');
        tallerBasico.setFechaInicio(LocalDate.now());
        tallerBasico.setFechaFin(LocalDate.now().plusDays(10));

        // Pre-cargar datos en los fakes
        usuarioRepo.usuarios.add(estudiante);
        usuarioRepo.usuarios.add(profesor);
        tallerRepo.talleres.add(tallerBasico);
        espacioRepo.espacios.add(espacioVacio);
    }

    // 1. Que un estudiante se pueda inscribir si hay cupos
    @Test
    public void siHayCupos_ElEstudianteSeInscribeExitosamente() throws Exception {
        inscripcionRepo.countInscritos = 5L; // Hay 5 inscritos de 10 cupos
        String resultado = tallerService.inscribirAlumno(1L, 2L);
        assertEquals("¡Inscripción exitosa! Lograste conseguir un cupo.", resultado);
        assertEquals(1, inscripcionRepo.savedInscripciones.size());
        assertEquals("INSCRITO", inscripcionRepo.savedInscripciones.get(0).getEstado());
    }

    // 2. Que el sistema lo mande a lista de espera si está lleno
    @Test
    public void siElTallerEstaLleno_DebeQuedarEnListaDeEspera() throws Exception {
        inscripcionRepo.countInscritos = 10L; // Taller lleno
        String resultado = tallerService.inscribirAlumno(1L, 2L);
        assertEquals("El taller está lleno. Has quedado en Lista de Espera.", resultado);
        assertEquals(1, inscripcionRepo.savedInscripciones.size());
        assertEquals("EN_ESPERA", inscripcionRepo.savedInscripciones.get(0).getEstado());
    }

    // 3. Que el sistema rechace la creación de un taller si hay un choque de horarios y espacios
    @Test
    public void alCrearTaller_SiHayConflictoDeEspacio_DebeLanzarExcepcion() {
        tallerRepo.simularConflicto = true;
        Exception exception = assertThrows(Exception.class, () -> {
            tallerService.crearTaller("Magia", "Desc", "Arte", 15, LocalDate.now(), LocalDate.now().plusDays(10), 'A', 5L, 1L);
        });
        assertEquals("Conflicto de horario: Ya existe un taller en el espacio 'null' en el bloque A durante estas fechas.", exception.getMessage());
    }

    @Test
    public void alInscribirAlumno_SiHayChoqueHorario_DebeLanzarExcepcion() {
        Taller tallerInscrito = new Taller();
        tallerInscrito.setNombre("Taller de Dibujo");
        tallerInscrito.setBloqueHorario('A');
        tallerInscrito.setFechaInicio(LocalDate.now());
        tallerInscrito.setFechaFin(LocalDate.now().plusDays(10));

        Inscripcion inscripcionActiva = new Inscripcion();
        inscripcionActiva.setTaller(tallerInscrito);
        inscripcionActiva.setEstado("INSCRITO");
        
        inscripcionRepo.inscripcionesUsuario.add(inscripcionActiva);

        Exception exception = assertThrows(Exception.class, () -> {
            tallerService.inscribirAlumno(1L, 2L);
        });

        assertEquals("No puedes inscribirte: Choque de horario con el taller 'Taller de Dibujo' en el bloque A.", exception.getMessage());
    }

    @Test
    public void siEsElProfesorDelTaller_DebeRetornarListaDeInscripciones() throws Exception {
        inscripcionRepo.inscripcionesTaller.add(new Inscripcion());
        inscripcionRepo.inscripcionesTaller.add(new Inscripcion());
        List<Inscripcion> alumnos = tallerService.obtenerInscripcionesPorTaller(1L, 5L);
        assertEquals(2, alumnos.size());
    }

    @Test
    public void siNoEsElProfesorDelTaller_DebeLanzarExcepcion() {
        Exception exception = assertThrows(Exception.class, () -> {
            tallerService.obtenerInscripcionesPorTaller(1L, 99L);
        });
        assertEquals("No tienes permiso para ver los alumnos de este taller.", exception.getMessage());
    }

    @Test
    public void alCancelarInscripcion_SiAlguienEstaEnEspera_DebePasarAInscrito() throws Exception {
        Inscripcion inscripcionActiva = new Inscripcion();
        inscripcionActiva.setEstado("INSCRITO");
        Inscripcion inscripcionEnEspera = new Inscripcion();
        inscripcionEnEspera.setEstado("EN_ESPERA");

        inscripcionRepo.inscripcionBuscada = inscripcionActiva;
        inscripcionRepo.inscripcionEnEspera = inscripcionEnEspera;

        tallerService.cancelarInscripcion(1L, 2L);

        assertEquals("CANCELADO", inscripcionActiva.getEstado());
        assertEquals("INSCRITO", inscripcionEnEspera.getEstado());
    }

    @Test
    public void alEliminarTaller_DebeEliminarInscripcionesYTaller() throws Exception {
        tallerService.eliminarTaller(1L);
        assertTrue(inscripcionRepo.deleteLlamado);
        assertTrue(tallerRepo.deleteLlamado);
    }

    // --- CLASES FAKE ---
    private static class FakeTallerRepository implements ITallerRepository {
        public List<Taller> talleres = new ArrayList<>();
        public boolean simularConflicto = false;
        public boolean deleteLlamado = false;

        @Override public void save(Taller t) {}
        @Override public List<Taller> findAll() { return talleres; }
        @Override public List<Taller> findByProfesor(Long id) { return talleres; }
        @Override public boolean existeConflicto(Long eId, Character b, LocalDate i, LocalDate f) { return simularConflicto; }
        @Override public Taller findById(Long id) { return talleres.stream().filter(t -> id.equals(t.getId())).findFirst().orElse(null); }
        @Override public void delete(Long id) { deleteLlamado = true; }
    }

    private static class FakeInscripcionRepository extends InscripcionRepository {
        public List<Inscripcion> savedInscripciones = new ArrayList<>();
        public Long countInscritos = 0L;
        public Inscripcion inscripcionBuscada = null;
        public Inscripcion inscripcionEnEspera = null;
        public List<Inscripcion> inscripcionesUsuario = new ArrayList<>();
        public List<Inscripcion> inscripcionesTaller = new ArrayList<>();
        public boolean deleteLlamado = false;

        @Override public void save(Inscripcion i) { savedInscripciones.add(i); }
        @Override public Long countByTallerAndEstado(Long tId, String e) { return countInscritos; }
        @Override public Inscripcion findByTallerAndUsuario(Long tId, Long uId) { return inscripcionBuscada; }
        @Override public Inscripcion findFirstEnEspera(Long tId) { return inscripcionEnEspera; }
        @Override public List<Inscripcion> findByUsuario(Long uId) { return inscripcionesUsuario; }
        @Override public List<Inscripcion> findByTallerId(Long tId) { return inscripcionesTaller; }
        @Override public void deleteByTallerId(Long tId) { deleteLlamado = true; }
    }

    private static class FakeUsuarioRepository extends UsuarioRepository {
        public List<Usuario> usuarios = new ArrayList<>();
        @Override public Usuario findById(Long id) { return usuarios.stream().filter(u -> id.equals(u.getId())).findFirst().orElse(null); }
    }

    private static class FakeEspacioRepository extends EspacioRepository {
        public List<Espacio> espacios = new ArrayList<>();
        @Override public List<Espacio> findAll() { return espacios; }
        @Override public Espacio findById(Long id) { return espacios.stream().filter(e -> id.equals(e.getId())).findFirst().orElse(null); }
    }
}
