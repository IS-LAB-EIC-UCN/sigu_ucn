package cl.ucn.app.service;

import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.security.PasswordHasher;
import cl.ucn.app.security.Pbkdf2PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String CORREO = "ana@ucn.cl";

    @Mock
    private UsuarioRepository usuarioRepository;

    private final PasswordHasher hasher = new Pbkdf2PasswordHasher(1_000);
    private AuthService servicio;

    @BeforeEach
    void preparar() {
        servicio = new AuthService(usuarioRepository, hasher);
    }

    private Usuario usuario(String passwordAlmacenada, boolean activo) {
        return new Usuario("Ana", CORREO, passwordAlmacenada, activo, new Rol("ESTUDIANTE"));
    }

    @Test
    void autenticaConContrasenaCifrada() {
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(usuario(hasher.hash("Clave2026"), true));
        assertNotNull(servicio.autenticar(CORREO, "Clave2026"));
    }

    @Test
    void rechazaContrasenaIncorrecta() {
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(usuario(hasher.hash("Clave2026"), true));
        assertNull(servicio.autenticar(CORREO, "Otra2026"));
    }

    @Test
    void elCorreoNoDistingueMayusculas() {
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(usuario(hasher.hash("Clave2026"), true));
        assertNotNull(servicio.autenticar("  ANA@UCN.CL ", "Clave2026"));
    }

    @Test
    void rechazaUsuarioInactivo() {
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(usuario(hasher.hash("Clave2026"), false));
        assertNull(servicio.autenticar(CORREO, "Clave2026"));
    }

    @Test
    void mantieneCompatibilidadConContrasenasAntiguasEnTextoPlano() {
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(usuario("admin123", true));
        assertNotNull(servicio.autenticar(CORREO, "admin123"));
    }
}
