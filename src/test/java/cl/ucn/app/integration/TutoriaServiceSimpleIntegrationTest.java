package cl.ucn.app.integration;

import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.AsignaturaRepository;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.service.TutoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración simples e independientes entre sí.
 * Cada test crea sus propios datos (con identificadores únicos vía UUID)
 * y no depende del orden de ejecución ni de estado dejado por otro test.
 * Esto facilita detectar de forma aislada si la conexión a la BD real
 * (PostgreSQL vía docker-compose) y el esquema están correctamente configurados.
 */
class TutoriaServiceSimpleIntegrationTest {

    private final TutoriaService service = new TutoriaService();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final AsignaturaRepository asignaturaRepository = new AsignaturaRepository();

    private String correoUnico(String prefijo) {
        return prefijo + "_" + UUID.randomUUID() + "@ucn.cl";
    }

    private String codigoUnico(String prefijo) {
        // máximo 20 caracteres según columna codigo VARCHAR(20)
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        return (prefijo + sufijo).toUpperCase();
    }

    @Test
    @DisplayName("1. Registrar un usuario lo persiste en la BD y se puede buscar por correo")
    void registrarUsuario_persisteYSePuedeBuscar() {
        String correo = correoUnico("estudiante");

        service.registrarUsuarioTutoria("Estudiante Simple", correo, "pass123", "ESTUDIANTE");

        Usuario encontrado = usuarioRepository.findByCorreo(correo);

        assertNotNull(encontrado);
        assertEquals("Estudiante Simple", encontrado.getNombre());
        assertEquals(correo, encontrado.getCorreo());
    }

    @Test
    @DisplayName("2. Crear una asignatura la persiste en la BD y se puede buscar por código")
    void crearAsignatura_persisteYSePuedeBuscar() {
        String codigo = codigoUnico("MAT");

        service.crearAsignatura(codigo, "Asignatura de prueba simple");

        Asignatura encontrada = asignaturaRepository.findByCodigo(codigo);

        assertNotNull(encontrada);
        assertEquals(codigo, encontrada.getCodigo());
        assertEquals("Asignatura de prueba simple", encontrada.getNombre());
    }

    @Test
    @DisplayName("3. No se puede crear dos asignaturas con el mismo código")
    void crearAsignatura_codigoDuplicado_lanzaExcepcion() {
        String codigo = codigoUnico("DUP");

        service.crearAsignatura(codigo, "Primera asignatura");

        assertThrows(IllegalArgumentException.class,
                () -> service.crearAsignatura(codigo, "Segunda asignatura con mismo código"));
    }

}