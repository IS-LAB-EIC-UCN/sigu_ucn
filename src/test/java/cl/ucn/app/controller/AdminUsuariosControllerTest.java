package cl.ucn.app.controller;
import cl.ucn.app.repository.UsuarioRepository;
import io.javalin.http.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class AdminUsuariosControllerTest {
    @Test void rechazaRolesNoAdministradoresSinConsultarDatos() {
        var repo=mock(UsuarioRepository.class);var c=new AdminUsuariosController(repo);var ctx=mock(Context.class,RETURNS_SELF);
        when(ctx.sessionAttribute("usuarioId")).thenReturn(2L);
        for(String rol:java.util.List.of("DOCENTE","ESTUDIANTE")) {
            when(ctx.sessionAttribute("usuarioRol")).thenReturn(rol);
            assertThrows(ForbiddenResponse.class,()->c.listar(ctx));assertThrows(ForbiddenResponse.class,()->c.crear(ctx));
        }
        verifyNoInteractions(repo);
    }
    @Test void administradorNoEscribeSinTokenValido() {
        var repo=mock(UsuarioRepository.class);var c=new AdminUsuariosController(repo);var ctx=mock(Context.class,RETURNS_SELF);
        when(ctx.sessionAttribute("usuarioId")).thenReturn(1L);when(ctx.sessionAttribute("usuarioRol")).thenReturn("ADMIN");
        when(ctx.sessionAttribute("csrfToken")).thenReturn("token");
        assertThrows(ForbiddenResponse.class,()->c.crear(ctx));verifyNoInteractions(repo);
    }
}
