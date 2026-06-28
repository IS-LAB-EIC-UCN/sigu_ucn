package cl.ucn.app.auth;

import cl.ucn.app.exceptions.AccesoDenegadoException;
import io.javalin.http.Context;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BibliotecaAuthFilterTest {

    @Mock
    private Context ctx;

    @Test
    public void testSinSesion_redirigeALogin() {
        lenient().when(ctx.sessionAttribute("usuarioNombre")).thenReturn(null);
        lenient().when(ctx.sessionAttribute("bibliotecaCurrentUser")).thenReturn(null);
        assertFalse(BibliotecaAuthFilter.requireRol(ctx, Set.of("ADMIN")));
        verify(ctx).redirect("/login");
    }

    @Test
    public void testRolNoPermitido_lanzaAccesoDenegado() {
        lenient().when(ctx.sessionAttribute("usuarioNombre")).thenReturn("Docente");
        lenient().when(ctx.sessionAttribute("bibliotecaCurrentUser")).thenReturn("Docente");
        lenient().when(ctx.sessionAttribute("usuarioRol")).thenReturn("ESTUDIANTE");
        AccesoDenegadoException ex = assertThrows(AccesoDenegadoException.class, () ->
                BibliotecaAuthFilter.requireRol(ctx, Set.of("ADMIN")));
        assertTrue(ex.getMessage().toLowerCase().contains("permisos"));
    }

    @Test
    public void testRolPermitido_pasa() {
        lenient().when(ctx.sessionAttribute("usuarioNombre")).thenReturn("Admin");
        lenient().when(ctx.sessionAttribute("bibliotecaCurrentUser")).thenReturn("Admin");
        lenient().when(ctx.sessionAttribute("usuarioRol")).thenReturn("ADMIN");
        assertTrue(BibliotecaAuthFilter.requireRol(ctx, Set.of("ADMIN")));
    }
}
