package cl.ucn.app.service;

import cl.ucn.app.model.Inscripcion;
import cl.ucn.app.model.Taller;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.InscripcionRepository;
import cl.ucn.app.repository.ITallerRepository;
import cl.ucn.app.repository.UsuarioRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TallerServiceTest {

    private ITallerService tallerService;
    private EspacioRepository espacioRepoStub;

    @BeforeEach
    public void setup() {
        espacioRepoStub = new EspacioRepository() {
            @Override
            public List<cl.ucn.app.model.Espacio> findAll() { return List.of(); }
            @Override
            public cl.ucn.app.model.Espacio findById(Long id) { return null; }
        };

        ITallerRepository tallerRepoStub = new ITallerRepository() {
            @Override
            public void save(Taller taller) {}
            
            @Override
            public List<Taller> findAll() { return List.of(); }

            @Override
            public List<Taller> findByProfesor(Long profesorId) { return List.of(); }

            @Override
            public boolean existeConflicto(Long espacioId, Character bloque, java.time.LocalDate inicio, java.time.LocalDate fin) { return false; }
            
            @Override
            public Taller findById(Long id) {
                Taller tallerFalso = new Taller();
                tallerFalso.setCuposTotales(10); 
                tallerFalso.setEstado("ABIERTO");
                return tallerFalso;
            }

            @Override
            public void delete(Long id) {}
        };

        InscripcionRepository inscripcionRepoStub = new InscripcionRepository() {
            @Override
            public Inscripcion findByTallerAndUsuario(Long tallerId, Long usuarioId) {
                return null;
            }

            @Override
            public Long countByTallerAndEstado(Long tallerId, String estado) {
                return 10L; 
            }

            @Override
            public void save(Inscripcion inscripcion) {}
        };

        UsuarioRepository usuarioRepoStub = new UsuarioRepository() {
            @Override
            public Usuario findById(Long id) {
                return new Usuario(); 
            }
        };

        tallerService = new TallerServiceImpl(tallerRepoStub, inscripcionRepoStub, usuarioRepoStub, espacioRepoStub);
    }

    @Test
    public void siElTallerEstaLleno_DebeQuedarEnListaDeEspera() throws Exception {
        String resultado = tallerService.inscribirAlumno(1L, 2L);
        assertEquals("El taller está lleno. Has quedado en Lista de Espera.", resultado);
    }

    @Test
    public void siEsElProfesorDelTaller_DebeRetornarListaDeInscripciones() throws Exception {
        ITallerRepository tallerRepoStub = new ITallerRepository() {
            @Override
            public void save(Taller taller) {}
            
            @Override
            public List<Taller> findAll() { return List.of(); }

            @Override
            public List<Taller> findByProfesor(Long profesorId) { return List.of(); }

            @Override
            public boolean existeConflicto(Long espacioId, Character bloque, java.time.LocalDate inicio, java.time.LocalDate fin) { return false; }
            
            @Override
            public Taller findById(Long id) {
                Usuario profesor = new Usuario() {
                    @Override
                    public Long getId() { return 5L; }
                };

                Taller taller = new Taller();
                taller.setId(id);
                taller.setProfesor(profesor);
                return taller;
            }

            @Override
            public void delete(Long id) {}
        };

        InscripcionRepository inscripcionRepoStub = new InscripcionRepository() {
            @Override
            public java.util.List<Inscripcion> findByTallerId(Long tallerId) {
                return java.util.List.of(new Inscripcion(), new Inscripcion());
            }
        };

        UsuarioRepository usuarioRepoStub = new UsuarioRepository() {
            @Override
            public Usuario findById(Long id) {
                return new Usuario();
            }
        };

        ITallerService serviceTest = new TallerServiceImpl(tallerRepoStub, inscripcionRepoStub, usuarioRepoStub, new EspacioRepository() {
            @Override public List<cl.ucn.app.model.Espacio> findAll() { return List.of(); }
            @Override public cl.ucn.app.model.Espacio findById(Long id) { return null; }
        });

        java.util.List<Inscripcion> alumnos = serviceTest.obtenerInscripcionesPorTaller(1L, 5L);

        assertEquals(2, alumnos.size());
    }

    @Test
    public void siNoEsElProfesorDelTaller_DebeLanzarExcepcion() {
        ITallerRepository tallerRepoStub = new ITallerRepository() {
            @Override
            public void save(Taller taller) {}
            
            @Override
            public List<Taller> findAll() { return List.of(); }

            @Override
            public List<Taller> findByProfesor(Long profesorId) { return List.of(); }

            @Override
            public boolean existeConflicto(Long espacioId, Character bloque, java.time.LocalDate inicio, java.time.LocalDate fin) { return false; }
            
            @Override
            public Taller findById(Long id) {
                Usuario profesor = new Usuario() {
                    @Override
                    public Long getId() { return 5L; }
                };

                Taller taller = new Taller();
                taller.setId(id);
                taller.setProfesor(profesor);
                return taller;
            }

            @Override
            public void delete(Long id) {}
        };

        ITallerService serviceTest = new TallerServiceImpl(tallerRepoStub, new InscripcionRepository(), new UsuarioRepository(), new EspacioRepository() {
            @Override public List<cl.ucn.app.model.Espacio> findAll() { return List.of(); }
            @Override public cl.ucn.app.model.Espacio findById(Long id) { return null; }
        });

        try {
            serviceTest.obtenerInscripcionesPorTaller(1L, 99L);
        } catch (Exception e) {
            assertEquals("No tienes permiso para ver los alumnos de este taller.", e.getMessage());
        }
    }

    @Test
    public void alCrearTallerConCuposInvalidos_DebeLanzarExcepcion() {
        try {
            tallerService.crearTaller("Taller 1", "Desc", 0, java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(1), 'A', 1L, null);
        } catch (Exception e) {
            assertEquals("Los cupos deben ser mayores a cero.", e.getMessage());
        }
    }

    @Test
    public void alEliminarTaller_DebeEliminarInscripcionesYTaller() throws Exception {
        final boolean[] tallerEliminado = {false};
        final boolean[] inscripcionesEliminadas = {false};

        ITallerRepository tallerRepoStubLocal = new ITallerRepository() {
            @Override public void save(Taller taller) {}
            @Override public List<Taller> findAll() { return List.of(); }
            @Override public List<Taller> findByProfesor(Long profesorId) { return List.of(); }
            @Override public boolean existeConflicto(Long espacioId, Character bloque, java.time.LocalDate inicio, java.time.LocalDate fin) { return false; }
            @Override public Taller findById(Long id) { return new Taller(); }
            @Override public void delete(Long id) { tallerEliminado[0] = true; }
        };

        InscripcionRepository inscripcionRepoStubLocal = new InscripcionRepository() {
            @Override public void save(Inscripcion inscripcion) {}
            @Override public Long countByTallerAndEstado(Long tallerId, String estado) { return 0L; }
            @Override public Inscripcion findByTallerAndUsuario(Long tallerId, Long usuarioId) { return null; }
            @Override public Inscripcion findFirstEnEspera(Long tallerId) { return null; }
            @Override public List<Inscripcion> findByUsuario(Long usuarioId) { return List.of(); }
            @Override public List<Inscripcion> findByTallerId(Long tallerId) { return List.of(); }
            @Override public void deleteByTallerId(Long tallerId) { inscripcionesEliminadas[0] = true; }
        };

        ITallerService serviceTest = new TallerServiceImpl(tallerRepoStubLocal, inscripcionRepoStubLocal, new UsuarioRepository(), new EspacioRepository() {
            @Override public List<cl.ucn.app.model.Espacio> findAll() { return List.of(); }
            @Override public cl.ucn.app.model.Espacio findById(Long id) { return null; }
        });

        serviceTest.eliminarTaller(1L);

        assertTrue(inscripcionesEliminadas[0], "Deberia haber llamado a eliminar las inscripciones");
        assertTrue(tallerEliminado[0], "Deberia haber llamado a eliminar el taller");
    }

    @Test
    public void alCrearTallerConFechasInvalidas_DebeLanzarExcepcion() {
        try {
            tallerService.crearTaller("Taller 1", "Desc", 10, java.time.LocalDate.now().plusDays(5), java.time.LocalDate.now(), 'A', 1L, null);
        } catch (Exception e) {
            assertEquals("La fecha de inicio no puede ser después de la fecha de fin.", e.getMessage());
        }
    }

    @Test
    public void alCrearTallerConProfesorValido_DebeRetornarTaller() throws Exception {
        Usuario profesor = new Usuario();
        cl.ucn.app.model.Rol rolDocente = new cl.ucn.app.model.Rol();
        rolDocente.setNombre("DOCENTE");
        profesor.setRol(rolDocente);

        UsuarioRepository usuarioRepoStub = new UsuarioRepository() {
            @Override
            public Usuario findById(Long id) {
                return profesor;
            }
        };

        ITallerRepository tallerRepoStub = new ITallerRepository() {
            @Override
            public void save(Taller taller) {}
            @Override
            public List<Taller> findAll() { return List.of(); }
            @Override
            public List<Taller> findByProfesor(Long profesorId) { return List.of(); }
            @Override
            public boolean existeConflicto(Long espacioId, Character bloque, java.time.LocalDate inicio, java.time.LocalDate fin) { return false; }
            @Override
            public Taller findById(Long id) { return null; }
            @Override
            public void delete(Long id) {}
        };

        ITallerService serviceTest = new TallerServiceImpl(tallerRepoStub, new InscripcionRepository(), usuarioRepoStub, new EspacioRepository() {
            @Override public List<cl.ucn.app.model.Espacio> findAll() { return List.of(); }
            @Override public cl.ucn.app.model.Espacio findById(Long id) { return null; }
        });

        Taller taller = serviceTest.crearTaller("Magia", "Desc", 15, java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(2), 'B', 1L, null);
        assertEquals("Magia", taller.getNombre());
        assertEquals(15, taller.getCuposTotales());
        assertEquals("ABIERTO", taller.getEstado());
    }

    @Test
    public void alCancelarInscripcion_SiAlguienEstaEnEspera_DebePasarAInscrito() throws Exception {
        Inscripcion inscripcionActiva = new Inscripcion();
        inscripcionActiva.setEstado("INSCRITO");

        Inscripcion inscripcionEnEspera = new Inscripcion();
        inscripcionEnEspera.setEstado("EN_ESPERA");

        InscripcionRepository inscripcionRepoStub = new InscripcionRepository() {
            @Override
            public Inscripcion findByTallerAndUsuario(Long tallerId, Long usuarioId) {
                return inscripcionActiva;
            }
            @Override
            public Inscripcion findFirstEnEspera(Long tallerId) {
                return inscripcionEnEspera;
            }
            @Override
            public void save(Inscripcion inscripcion) {}
        };

        ITallerService serviceTest = new TallerServiceImpl(new ITallerRepository() {
            @Override public void save(Taller taller) {}
            @Override public List<Taller> findAll() { return List.of(); }
            @Override public List<Taller> findByProfesor(Long profesorId) { return List.of(); }
            @Override public boolean existeConflicto(Long espacioId, Character bloque, java.time.LocalDate inicio, java.time.LocalDate fin) { return false; }
            @Override public Taller findById(Long id) { return new Taller(); }
            @Override public void delete(Long id) {}
        }, inscripcionRepoStub, new UsuarioRepository(), new EspacioRepository() {
            @Override public List<cl.ucn.app.model.Espacio> findAll() { return List.of(); }
            @Override public cl.ucn.app.model.Espacio findById(Long id) { return null; }
        });

        serviceTest.cancelarInscripcion(1L, 2L);

        assertEquals("CANCELADO", inscripcionActiva.getEstado());
        assertEquals("INSCRITO", inscripcionEnEspera.getEstado());
    }
}
