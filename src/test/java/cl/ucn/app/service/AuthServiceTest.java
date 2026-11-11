package cl.ucn.app.service;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class AuthServiceTest {
    @Test void normalizaCorreoYConservaPassword() {
        UsuarioRepository repo = mock(UsuarioRepository.class);
        Usuario usuario = mock(Usuario.class);
        when(repo.findByCorreo("admin@sigu.cl")).thenReturn(usuario);
        when(usuario.getActivo()).thenReturn(true);
        when(usuario.getPassword()).thenReturn("admin123");
        AuthService service = new AuthService(repo);
        assertSame(usuario, service.autenticar("  ADMIN@sigu.cl  ", "admin123"));
        assertNull(service.autenticar("admin@sigu.cl", "incorrecta"));
        assertNull(service.autenticar("admin@sigu.cl", " admin123 "));
        when(usuario.getActivo()).thenReturn(false);
        assertNull(service.autenticar("admin@sigu.cl", "admin123"));
    }
    @Test void rechazaCamposAusentesSinConsultarBase() {
        UsuarioRepository repo = mock(UsuarioRepository.class);
        AuthService service = new AuthService(repo);
        assertNull(service.autenticar(null, "clave"));
        assertNull(service.autenticar("admin@sigu.cl", null));
        assertNull(service.autenticar(" ", "clave"));
        verifyNoInteractions(repo);
    }
}
