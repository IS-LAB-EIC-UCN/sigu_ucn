package cl.ucn.app.service;

import cl.ucn.app.model.Inscripcion;
import cl.ucn.app.model.Taller;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.InscripcionRepository;
import cl.ucn.app.repository.TallerRepository;
import cl.ucn.app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TallerServiceTest {

    private TallerService tallerService;

    @BeforeEach
    public void setup() {

        TallerRepository tallerRepoStub = new TallerRepository() {
            @Override
            public Taller findById(Long id) {
                Taller tallerFalso = new Taller();
                tallerFalso.setCuposTotales(10); 
                tallerFalso.setEstado("ABIERTO");
                return tallerFalso;
            }
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
            public void save(Inscripcion inscripcion) {

            }
        };

        UsuarioRepository usuarioRepoStub = new UsuarioRepository() {
            @Override
            public Usuario findById(Long id) {
                return new Usuario(); 
            }
        };

        tallerService = new TallerService(tallerRepoStub, inscripcionRepoStub, usuarioRepoStub);
    }

    @Test
    public void siElTallerEstaLleno_DebeQuedarEnListaDeEspera() throws Exception {

        String resultado = tallerService.inscribirAlumno(1L, 2L);

 
        assertEquals("El taller está lleno. Has quedado en Lista de Espera.", resultado);
    }
}
