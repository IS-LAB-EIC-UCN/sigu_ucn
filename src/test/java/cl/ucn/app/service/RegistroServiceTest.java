package cl.ucn.app.service;

import cl.ucn.app.dto.RegistroForm;
import cl.ucn.app.exception.RegistroInvalidoException;
import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.RolRepository;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private PasswordHasher passwordHasher;

    private RegistroService servicio;

    @BeforeEach
    void preparar() {
        servicio = new RegistroService(usuarioRepository, rolRepository, passwordHasher,
                Set.of("ucn.cl", "alumnos.ucn.cl"));
    }

    private RegistroForm form(String nombre, String correo, String password, String confirmacion, String rol) {
        return new RegistroForm(nombre, correo, password, confirmacion, rol);
    }

    private RegistroForm formValido() {
        return form("Ana Pérez", "ana.perez@alumnos.ucn.cl", "Clave2026", "Clave2026", "ESTUDIANTE");
    }

    private RegistroInvalidoException registrarConError(RegistroForm form) {
        RegistroInvalidoException e = assertThrows(RegistroInvalidoException.class, () -> servicio.registrar(form));
        verify(usuarioRepository, never()).save(any());
        return e;
    }

    @Test
    void registraUsuarioValidoConContrasenaCifrada() {
        when(usuarioRepository.findByCorreo("ana.perez@alumnos.ucn.cl")).thenReturn(null);
        when(rolRepository.findByNombre("ESTUDIANTE")).thenReturn(new Rol("ESTUDIANTE"));
        when(passwordHasher.hash("Clave2026")).thenReturn("pbkdf2_sha256$hash");

        servicio.registrar(formValido());

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(guardado.capture());
        assertEquals("pbkdf2_sha256$hash", guardado.getValue().getPassword());
        assertEquals("ESTUDIANTE", guardado.getValue().getRol().getNombre());
        assertTrue(guardado.getValue().getActivo());
    }

    @Test
    void normalizaElCorreoAMinusculasYSinEspacios() {
        when(rolRepository.findByNombre("DOCENTE")).thenReturn(new Rol("DOCENTE"));
        when(passwordHasher.hash(any())).thenReturn("hash");

        servicio.registrar(form("Luis Rojas", "  Luis.Rojas@UCN.cl ", "Clave2026", "Clave2026", "DOCENTE"));

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(guardado.capture());
        assertEquals("luis.rojas@ucn.cl", guardado.getValue().getCorreo());
    }

    @Test
    void rechazaCorreoYaRegistrado() {
        when(usuarioRepository.findByCorreo("ana.perez@alumnos.ucn.cl")).thenReturn(new Usuario());
        RegistroInvalidoException e = registrarConError(formValido());
        assertTrue(e.getErrores().containsKey("correo"));
    }

    @Test
    void rechazaCorreoDeDominioNoInstitucional() {
        RegistroInvalidoException e = registrarConError(
                form("Ana", "ana@gmail.com", "Clave2026", "Clave2026", "ESTUDIANTE"));
        assertTrue(e.getErrores().get("correo").contains("institucional"));
    }

    @Test
    void rechazaCorreoConFormatoInvalido() {
        RegistroInvalidoException e = registrarConError(
                form("Ana", "ana-sin-arroba", "Clave2026", "Clave2026", "ESTUDIANTE"));
        assertTrue(e.getErrores().containsKey("correo"));
    }

    @Test
    void rechazaContrasenaCortaOSinNumeros() {
        assertTrue(registrarConError(form("Ana", "ana@ucn.cl", "Ab1", "Ab1", "ESTUDIANTE"))
                .getErrores().containsKey("password"));
        assertTrue(registrarConError(form("Ana", "ana@ucn.cl", "SoloLetras", "SoloLetras", "ESTUDIANTE"))
                .getErrores().containsKey("password"));
    }

    @Test
    void rechazaConfirmacionDistinta() {
        RegistroInvalidoException e = registrarConError(
                form("Ana", "ana@ucn.cl", "Clave2026", "Clave2027", "ESTUDIANTE"));
        assertTrue(e.getErrores().containsKey("confirmacion"));
    }

    @Test
    void noPermiteAutoRegistrarseComoAdministrador() {
        RegistroInvalidoException e = registrarConError(
                form("Ana", "ana@ucn.cl", "Clave2026", "Clave2026", "ADMIN"));
        assertTrue(e.getErrores().containsKey("rol"));
    }

    @Test
    void informaTodosLosErroresALaVez() {
        RegistroInvalidoException e = registrarConError(form("", "", "", "x", ""));
        assertEquals(Set.of("nombre", "correo", "password", "confirmacion", "rol"), e.getErrores().keySet());
    }
}
